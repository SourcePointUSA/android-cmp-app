package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Legislation.CCPA
import com.sourcepoint.cmplibrary.exception.Legislation.GDPR
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

/**
 * Factory method to create an instance of a [Service] using its implementation
 * @param nc is an instance of [NetworkClient]
 * @param ds is an instance of [DataStorage]
 * @param cm is an instance of [CampaignManager]
 * @return an instance of the [ServiceImpl] implementation
 */
internal fun Service.Companion.create(
    nc: NetworkClient,
    ds: DataStorage,
    cm: CampaignManager
): Service = ServiceImpl(nc, ds, cm)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val nc: NetworkClient,
    private val ds: DataStorage,
    private val cm: CampaignManager
) : Service, NetworkClient by nc, CampaignManager by cm {

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) {
        nc.getMessage(
            messageReq,
            { messageResp ->
                messageResp.campaigns.forEach { cr ->
                    when (cr) {
                        is Gdpr -> {
                            cm.saveGdpr(cr)
                            cr.userConsent?.let { uc -> cm.saveGDPRConsent(uc) }
                        }
                        is Ccpa -> {
                            cm.saveCcpa(cr)
                            cr.userConsent.let { uc -> cm.saveCCPAConsent(uc) }
                        }
                    }
                }
                pSuccess(messageResp)
            },
            pError
        )
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        nc.getNativeMessage(
            messageReq,
            { nativeMessageResp ->
                success(nativeMessageResp)
                nativeMessageResp.msgJSON
                // TODO save the data into the local storage
            },
            error
        )
    }

    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {
        nc.getNativeMessageK(
            messageReq,
            { nativeMessageResp ->
                success(nativeMessageResp)
                nativeMessageResp.msg
                // TODO save the data into the local storage
            },
            error
        )
    }

    override fun sendConsent(legislation: Legislation, consentReq: JSONObject, success: (ConsentResp) -> Unit, error: (Throwable) -> Unit) {
        nc.sendConsent(
            legislation,
            consentReq,
            { consentResp ->
                success(consentResp.copy(legislation = legislation))
                when (legislation) {
                    GDPR -> {
                        consentResp.content
                            .toTreeMap()
                            .getMap("userConsent")
                            ?.toGDPRUserConsent()
                            ?.let { cm.saveGDPRConsent(it) }
                    }
                    CCPA -> {
                        consentResp.content
                            .toTreeMap()
                            .getMap("userConsent")
                            ?.toCCPAUserConsent()
                            ?.let { cm.saveCCPAConsent(it) }
                    }
                }
            },
            error
        )
    }
}

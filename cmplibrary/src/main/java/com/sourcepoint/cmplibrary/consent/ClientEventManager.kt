package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.UnitySpClient
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.check

internal interface ClientEventManager {
    fun setCampaignsToProcess(numberOfCampaigns: Int)
    fun registerConsentResponse()
    fun setAction(action: ConsentActionImpl)
    fun setAction(action: NativeMessageActionType)
    fun checkIfAllCampaignsWereProcessed()
    companion object
}

internal fun ClientEventManager.Companion.create(
    logger: Logger,
    executor: ExecutorManager,
    consentManagerUtils: ConsentManagerUtils,
    spClient: SpClient
): ClientEventManager = ClientEventManagerImpl(logger, executor, spClient, consentManagerUtils)

private class ClientEventManagerImpl(
    val logger: Logger,
    val executor: ExecutorManager,
    val spClient: SpClient,
    val consentManagerUtils: ConsentManagerUtils,
) : ClientEventManager {

    private var campaignsToProcess: Int = Int.MAX_VALUE
    private var isFirstLayerMessage = true

    override fun setCampaignsToProcess(numberOfCampaigns: Int) {
        campaignsToProcess = numberOfCampaigns
    }

    override fun setAction(action: ConsentActionImpl) {
        executor.executeOnSingleThread {
            when (action.actionType) {
                ActionType.SHOW_OPTIONS -> {
                    isFirstLayerMessage = false
                }
                ActionType.CUSTOM,
                ActionType.MSG_CANCEL,
                ActionType.PM_DISMISS -> {
                    if (isFirstLayerMessage) {
                        campaignsToProcess--
                    }
                    isFirstLayerMessage = true
                }
                ActionType.GET_MSG_ERROR, ActionType.GET_MSG_NOT_CALLED -> {
                    campaignsToProcess = 0
                }
                else -> {
                    // do nothing
                }
            }
            checkIfAllCampaignsWereProcessed()
        }
    }

    override fun setAction(action: NativeMessageActionType) {
        executor.executeOnSingleThread {
            when (action) {
                NativeMessageActionType.ACCEPT_ALL,
                NativeMessageActionType.REJECT_ALL,
                NativeMessageActionType.SHOW_OPTIONS -> {
                }
                NativeMessageActionType.UNKNOWN -> {
                }
                NativeMessageActionType.MSG_CANCEL -> {
                    if (campaignsToProcess > 0) campaignsToProcess--
                }
                NativeMessageActionType.GET_MSG_ERROR, NativeMessageActionType.GET_MSG_NOT_CALLED -> {
                    campaignsToProcess = 0
                }
            }
            checkIfAllCampaignsWereProcessed()
        }
    }

    override fun checkIfAllCampaignsWereProcessed() {
        if (campaignsToProcess <= 0) {
            campaignsToProcess = Int.MAX_VALUE

            val spConsent: SPConsents? = getSPConsents().getOrNull()
            val spConsentString = spConsent
                ?.let {
                    spClient.onSpFinished(it)
                    (spClient as? UnitySpClient)?.onSpFinished(it.toJsonObject().toString())
                    it.toJsonObject().toString()
                }
                ?: run {
                    spClient.onError(Throwable("Something went wrong during the consent fetching process."))
                    "{}"
                }
            logger.clientEvent(
                event = "onSpFinish",
                msg = "All campaigns have been processed.",
                content = spConsentString
            )
        }
    }

    override fun registerConsentResponse() {
        campaignsToProcess--
    }

    private fun getSPConsents() = check<SPConsents> {
        val gdprCached = consentManagerUtils.gdprConsentOptimized.getOrNull()
        val ccpaCached = consentManagerUtils.ccpaConsentOptimized.getOrNull()
        val usNatCached = consentManagerUtils.usNatConsent.getOrNull()
        SPConsents(
            gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
            ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) },
            usNat = usNatCached?.let { usNatConsent -> SpUsNatConsent(consent = usNatConsent) },
        )
    }
}

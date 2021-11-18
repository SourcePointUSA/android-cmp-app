package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.model.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import java.util.* //ktlint-disable

internal interface ConsentManager {
    var localStateStatus: LocalStateStatus
    fun enqueueConsent(consentActionImpl: ConsentActionImpl)
    fun enqueueConsent(nativeConsentAction: NativeConsentAction)
    fun sendStoredConsentToClient()
    fun sendConsent(
        actionImpl: ConsentActionImpl,
        localState: String
    )

    val enqueuedActions: Int
    val gdprUuid: String?
    val ccpaUuid: String?
    var sPConsentsSuccess: ((SPConsents) -> Unit)?
    var sPConsentsError: ((Throwable) -> Unit)?

    companion object
}

internal fun ConsentManager.Companion.create(
    service: Service,
    consentManagerUtils: ConsentManagerUtils,
    env: Env,
    logger: Logger,
    dataStorage: DataStorage,
    executorManager: ExecutorManager,
    clientEventManager: ClientEventManager
): ConsentManager = ConsentManagerImpl(service, consentManagerUtils, logger, env, dataStorage, executorManager, clientEventManager)

private class ConsentManagerImpl(
    private val service: Service,
    private val consentManagerUtils: ConsentManagerUtils,
    private val logger: Logger,
    private val env: Env,
    private val dataStorage: DataStorage,
    private val executorManager: ExecutorManager,
    private val clientEventManager: ClientEventManager
) : ConsentManager {

    override var sPConsentsSuccess: ((SPConsents) -> Unit)? = null
    override var sPConsentsError: ((Throwable) -> Unit)? = null
    override var localStateStatus: LocalStateStatus = LocalStateStatus.Absent
        set(value) {
            field = value
            changeLocalState(value)
        }
        get() = dataStorage.getLocalState()?.let { LocalStateStatus.Present(it) } ?: LocalStateStatus.Absent
    private val consentQueueImpl: Queue<ConsentActionImpl> = LinkedList()
    override val enqueuedActions: Int
        get() = consentQueueImpl.size

    override val gdprUuid: String?
        get() = dataStorage.getGdprConsentUuid()

    override val ccpaUuid: String?
        get() = dataStorage.getCcpaConsentUuid()

    override fun enqueueConsent(consentActionImpl: ConsentActionImpl) {
        consentQueueImpl.offer(consentActionImpl)
        val lState: LocalStateStatus.Present? = localStateStatus as? LocalStateStatus.Present
        if (lState != null) {
            val localState = lState.value
            val action = consentQueueImpl.poll()
            sendConsent(action, localState)
        }
    }

    override fun enqueueConsent(nativeConsentAction: NativeConsentAction) {
        enqueueConsent(nativeConsentAction.toConsentAction())
    }

    fun changeLocalState(newState: LocalStateStatus) {
        when (newState) {
            is LocalStateStatus.Present -> {
                if (consentQueueImpl.isNotEmpty()) {
                    val localState = newState.value
                    val action = consentQueueImpl.poll()
                    sendConsent(action, localState)
                    localStateStatus = LocalStateStatus.Consumed
                }
            }
            LocalStateStatus.Absent,
            LocalStateStatus.Consumed -> return
        }
    }

    override fun sendStoredConsentToClient() {
        check {
            val ccpaCached = consentManagerUtils.getCcpaConsent().getOrNull()
            val gdprCached = consentManagerUtils.getGdprConsent().getOrNull()
            SPConsents(
                gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
                ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) }
            ).let { sPConsentsSuccess?.invoke(it) }
        }
    }

    override fun sendConsent(actionImpl: ConsentActionImpl, localState: String) {
        executorManager.executeOnSingleThread {
            when (val either = service.sendConsent(localState, actionImpl, env, actionImpl.privacyManagerId)) {
                is Right -> {
                    val updatedLocalState = LocalStateStatus.Present(either.r.localState)
                    val sPConsents = responseConsentHandler(either, actionImpl, consentManagerUtils)
                    sPConsentsSuccess?.invoke(sPConsents)
                    this.localStateStatus = updatedLocalState
                    clientEventManager.storedConsent()
                }
                is Left -> sPConsentsError?.invoke(either.t)
            }
        }
    }
}

internal sealed class LocalStateStatus {
    data class Present(val value: String) : LocalStateStatus()
    object Absent : LocalStateStatus()
    object Consumed : LocalStateStatus()
}

internal fun responseConsentHandler(
    either: Right<ConsentResp>,
    actionImpl: ConsentActionImpl,
    consentManagerUtils: ConsentManagerUtils
): SPConsents {
    val map: Map<String, Any?> = either.r.content.toTreeMap()
    val uuid: String? = either.r.uuid
    return map.getMap("userConsent")
        ?.let {
            when (actionImpl.campaignType) {
                CampaignType.GDPR -> it.toGDPRUserConsent(uuid = uuid).let { gdprConsent ->
                    val ccpaCached = consentManagerUtils.getCcpaConsent().getOrNull()
                    SPConsents(
                        gdpr = SPGDPRConsent(consent = gdprConsent),
                        ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) }
                    )
                }
                CampaignType.CCPA -> it.toCCPAUserConsent(uuid = uuid).let { ccpaConsent ->
                    val gdprCached = consentManagerUtils.getGdprConsent().getOrNull()
                    SPConsents(
                        gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
                        ccpa = SPCCPAConsent(consent = ccpaConsent)
                    )
                }
            }
        } ?: SPConsents()
}

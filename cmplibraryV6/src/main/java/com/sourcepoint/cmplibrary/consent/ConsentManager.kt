package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.model.toGDPRUserConsent
import com.sourcepoint.cmplibrary.model.toTreeMap
import java.util.* //ktlint-disable

internal interface ConsentManager {
    var localStateStatus: LocalStateStatus
    fun enqueueConsent(consentAction: ConsentAction)
    fun sendConsent(
        action: ConsentAction,
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
    executorManager: ExecutorManager
): ConsentManager = ConsentManagerImpl(service, consentManagerUtils, logger, env, dataStorage, executorManager)

private class ConsentManagerImpl(
    private val service: Service,
    private val consentManagerUtils: ConsentManagerUtils,
    private val logger: Logger,
    private val env: Env,
    private val dataStorage: DataStorage,
    private val executorManager: ExecutorManager
) : ConsentManager {

    override var sPConsentsSuccess: ((SPConsents) -> Unit)? = null
    override var sPConsentsError: ((Throwable) -> Unit)? = null
    override var localStateStatus: LocalStateStatus = LocalStateStatus.Absent
        set(value) {
            field = value
            changeLocalState(value)
        }
    private val consentQueue2: Queue<ConsentAction> = LinkedList()
    override val enqueuedActions: Int
        get() = consentQueue2.size

    override val gdprUuid: String?
        get() = dataStorage.getGdprConsentUuid()

    override val ccpaUuid: String?
        get() = dataStorage.getCcpaConsentUuid()

    override fun enqueueConsent(consentAction: ConsentAction) {
        consentQueue2.offer(consentAction)
        val lState: LocalStateStatus.Present? = localStateStatus as? LocalStateStatus.Present
        if (lState != null) {
            val localState = lState.value
            val action = consentQueue2.poll()
            sendConsent(action, localState)
        }
    }

    fun changeLocalState(newState: LocalStateStatus) {
        when (newState) {
            is LocalStateStatus.Present -> {
                if (consentQueue2.isNotEmpty()) {
                    val localState = newState.value
                    val action = consentQueue2.poll()
                    sendConsent(action, localState)
                    localStateStatus = LocalStateStatus.Consumed
                }
            }
            LocalStateStatus.Absent,
            LocalStateStatus.Consumed -> return
        }
    }

    override fun sendConsent(action: ConsentAction, localState: String) {
        executorManager.executeOnSingleThread {
            when (val either = service.sendConsent(localState, action, env, action.privacyManagerId)) {
                is Right -> {
                    val updatedLocalState = LocalStateStatus.Present(either.r.localState)
                    val sPConsents = responseHandler(either, action)
                    sPConsentsSuccess?.invoke(sPConsents)
                    this.localStateStatus = updatedLocalState
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

internal fun responseHandler(either: Right<ConsentResp>, action: ConsentAction): SPConsents {
    val map: Map<String, Any?> = either.r.content.toTreeMap()
    return map.getMap("userConsent")
        ?.let {
            when (action.legislation) {
                Legislation.GDPR -> it.toGDPRUserConsent().let { gdprConsent ->
                    SPConsents(gdpr = SPGDPRConsent(consent = gdprConsent, applies = true))
                }
                Legislation.CCPA -> it.toCCPAUserConsent().let { ccpaConsent ->
                    SPConsents(ccpa = SPCCPAConsent(consent = ccpaConsent, applies = true))
                }
            }
        } ?: SPConsents()
}

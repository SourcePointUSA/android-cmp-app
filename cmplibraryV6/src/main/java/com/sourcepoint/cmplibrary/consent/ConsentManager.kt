package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.SPConsents
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.ExecutorManager
import java.util.* //ktlint-disable

internal interface ConsentManager {
    var localStateStatus: LocalStateStatus
    fun enqueueConsent2(consentAction: ConsentAction)
    fun sendConsent2(
        action: ConsentAction,
        localState: String
    )
    val enqueuedActions: Int
    var sPConsentsSuccess: ((SPConsents) -> Unit)?
    var sPConsentsError: ((Throwable) -> Unit)?

    companion object
}

internal fun ConsentManager.Companion.create(
    service: Service,
    consentManagerUtils: ConsentManagerUtils,
    env: Env,
    logger: Logger,
    executorManager: ExecutorManager
): ConsentManager = ConsentManagerImpl(service, consentManagerUtils, logger, env, executorManager)

private class ConsentManagerImpl(
    private val service: Service,
    private val consentManagerUtils: ConsentManagerUtils,
    private val logger: Logger,
    private val env: Env,
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

//    override var localState: String? = null
    private val consentQueue: Queue<ConsentAction> = LinkedList()

//    override fun enqueueConsent(consentAction: ConsentAction) {
//        consentQueue.offer(consentAction)
//    }
//
//    override fun sendConsent(
//        success: (SPConsents) -> Unit,
//        error: (Throwable) -> Unit
//    ) {
//        executorManager.executeOnSingleThread {
//            var sPConsents = SPConsents()
//            while (!consentQueue.isEmpty()) {
//                val action = consentQueue.poll()
//                when (val either = service.sendConsent(action, env)) {
//                    is Right -> {
//                        sPConsents = responseHandler(either, action, sPConsents)
//                        consentManagerUtils.saveGdprConsent(either.r.content)
//                    }
//                    is Left -> error(either.t)
//                }
//            }
//            /** send the final response to the client */
//            success(sPConsents)
//        }
//    }

    override fun enqueueConsent2(consentAction: ConsentAction) {
        consentQueue2.offer(consentAction)
        val lState: LocalStateStatus.Present? = localStateStatus as? LocalStateStatus.Present
        if (lState != null) {
            val localState = lState.value
            val action = consentQueue2.poll()
            sendConsent2(action, localState)
        }
    }

    fun changeLocalState(newState: LocalStateStatus) {
        when (newState) {
            is LocalStateStatus.Present -> {
                if (consentQueue2.isNotEmpty()) {
                    val localState = newState.value
                    val action = consentQueue2.poll()
                    sendConsent2(action, localState)
                    localStateStatus = LocalStateStatus.Consumed
                }
            }
            LocalStateStatus.Absent,
            LocalStateStatus.Consumed -> return
        }
    }

    override fun sendConsent2(action: ConsentAction, localState: String) {
        executorManager.executeOnSingleThread {
            when (val either = service.sendConsent(localState, action, env)) {
                is Right -> {
                    val updatedLocalState = LocalStateStatus.Present(either.r.localState)
                    val sPConsents = responseHandler(either, action)
                    sPConsentsSuccess?.invoke(sPConsents)
                    consentManagerUtils.saveGdprConsent(either.r.content)
                    this.localStateStatus = updatedLocalState
                }
                is Left -> sPConsentsError?.invoke(either.t)
            }
        }
    }
}

sealed class LocalStateStatus {
    data class Present(val value: String) : LocalStateStatus()
    object Absent : LocalStateStatus()
    object Consumed : LocalStateStatus()
}

internal fun responseHandler(either: Right<ConsentResp>, action: ConsentAction, sPConsents: SPConsents): SPConsents {
    val map: Map<String, Any?> = either.r.content.toTreeMap()
    return map.getMap("userConsent")
        ?.let {
            when (action.legislation) {
                Legislation.GDPR -> it.toGDPRUserConsent().let { gdprConsent ->
                    sPConsents.copy(gdpr = SPGDPRConsent(consent = gdprConsent, applies = true))
                }
                Legislation.CCPA -> it.toCCPAUserConsent().let { ccpaConsent ->
                    sPConsents.copy(ccpa = SPCCPAConsent(consent = ccpaConsent, applies = true))
                }
            }
        } ?: sPConsents
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

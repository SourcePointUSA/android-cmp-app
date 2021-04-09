package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
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
import java.util.* //ktlint-disable

internal interface ConsentManager {
    var localStateStatus: LocalStateStatus
    fun enqueueConsent2(consentAction: ConsentAction, env: Env)
    fun sendConsent2(
        action: ConsentAction,
        localState: String,
        env: Env
    )

    val enqueuedActions: Int
    var sPConsentsSuccess: ((SPConsents) -> Unit)?
    var sPConsentsError: ((Throwable) -> Unit)?

    companion object
}

internal fun ConsentManager.Companion.create(
    service: Service,
    consentManagerUtils: ConsentManagerUtils,
    logger: Logger,
    executorManager: ExecutorManager
): ConsentManager = ConsentManagerImpl(service, consentManagerUtils, logger, executorManager)

private class ConsentManagerImpl(
    private val service: Service,
    private val consentManagerUtils: ConsentManagerUtils,
    private val logger: Logger,
    private val executorManager: ExecutorManager
) : ConsentManager {

    override var sPConsentsSuccess: ((SPConsents) -> Unit)? = null
    override var sPConsentsError: ((Throwable) -> Unit)? = null
    override var localStateStatus: LocalStateStatus = LocalStateStatus.Absent
        set(value) {
            field = value
            when (value) {
                is LocalStateStatus.Present -> changeLocalState(value, value.env)
                LocalStateStatus.Absent,
                LocalStateStatus.Consumed -> return
            }
        }
    private val consentQueue2: Queue<Pair<ConsentAction, Env>> = LinkedList()
    override val enqueuedActions: Int
        get() = consentQueue2.size

    override fun enqueueConsent2(consentAction: ConsentAction, env: Env) {
        consentQueue2.offer(consentAction to env)
        val lState: LocalStateStatus.Present? = localStateStatus as? LocalStateStatus.Present
        if (lState != null) {
            val localState = lState.value
            val pair = consentQueue2.poll()
            sendConsent2(pair.first, localState, pair.second)
        }
    }

    fun changeLocalState(newState: LocalStateStatus, env: Env) {
        when (newState) {
            is LocalStateStatus.Present -> {
                if (consentQueue2.isNotEmpty()) {
                    val localState = newState.value
                    val pair = consentQueue2.poll()
                    sendConsent2(pair.first, localState, pair.second)
                    localStateStatus = LocalStateStatus.Consumed
                }
            }
            LocalStateStatus.Absent,
            LocalStateStatus.Consumed -> return
        }
    }

    override fun sendConsent2(action: ConsentAction, localState: String, env: Env) {
        executorManager.executeOnSingleThread {
            when (val either = service.sendConsent(localState, action, env)) {
                is Right -> {
                    val updatedLocalState = LocalStateStatus.Present(either.r.localState, env)
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
    data class Present(val value: String, val env: Env) : LocalStateStatus()
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

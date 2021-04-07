package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.SPConsents
import java.util.* //ktlint-disable

internal interface ConsentManager {

    fun enqueueConsent(
        consentAction: ConsentAction,
        env: Env
    )

    fun sendConsent(
        success: (SPConsents) -> Unit,
        error: (Throwable) -> Unit
    )

    companion object
}

internal fun ConsentManager.Companion.create(
    service: Service,
    consentManagerUtils: ConsentManagerUtils
): ConsentManager = ConsentManagerImpl(service, consentManagerUtils)

private class ConsentManagerImpl(
    private val service: Service,
    private val consentManagerUtils: ConsentManagerUtils
) : ConsentManager {

    private val campaignQueue: Queue<ConsentAction> = LinkedList()

    override fun enqueueConsent(
        consentAction: ConsentAction,
        env: Env
    ) {
    }

    override fun sendConsent(
        success: (SPConsents) -> Unit,
        error: (Throwable) -> Unit
    ) {
    }
}

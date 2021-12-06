package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.check

internal interface ClientEventManager {

    fun setCampaignNumber(campNum: Int)
    fun executingLoadPM()
    fun storedConsent()
    fun setAction(action: ConsentActionImpl)
    fun setAction(action: NativeMessageActionType)
    fun checkStatus()

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

    private var cNumber: Int = Int.MAX_VALUE
    private var storedConsent: Int = 0

    override fun setCampaignNumber(campNum: Int) {
        cNumber = campNum
        storedConsent = 0
    }

    override fun executingLoadPM() {
        cNumber = 1
        storedConsent = 0
    }

    override fun setAction(action: ConsentActionImpl) {
        executor.executeOnSingleThread {
            when (action.actionType) {
                ActionType.ACCEPT_ALL,
                ActionType.REJECT_ALL,
                ActionType.SAVE_AND_EXIT -> {
                }
                ActionType.SHOW_OPTIONS -> {
                }
                ActionType.UNKNOWN -> {
                }
                ActionType.CUSTOM,
                ActionType.MSG_CANCEL,
                ActionType.PM_DISMISS -> {
                    if (!action.requestFromPm || action.singleShotPM) {
                        if (cNumber > 0) cNumber--
                    }
                }
            }
            checkStatus()
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
                    if (cNumber > 0) cNumber--
                }
            }
            checkStatus()
        }
    }

    override fun checkStatus() {
        if (cNumber == storedConsent) {
            cNumber = Int.MAX_VALUE
            storedConsent = 0

            val spConsent: SPConsents? = getSPConsents().getOrNull()
            val spConsentString = spConsent
                ?.let {
                    spClient.onSpFinished(it)
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

    override fun storedConsent() {
        storedConsent++
    }

    private fun getSPConsents() = check<SPConsents> {
        val ccpaCached = consentManagerUtils.getCcpaConsent().getOrNull()
        val gdprCached = consentManagerUtils.getGdprConsent().getOrNull()
        SPConsents(
            gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
            ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) }
        )
    }
}

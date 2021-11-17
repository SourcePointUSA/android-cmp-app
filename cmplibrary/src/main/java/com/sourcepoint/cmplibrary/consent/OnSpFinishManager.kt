package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType

internal interface ClientManager {

    fun setCampaignNumber(campNum: Int)
    fun storedConsent()
    fun setAction(action: ConsentActionImpl)
    fun setAction(action: NativeMessageActionType)
    fun checkStatus()
    fun reset()

    companion object
}

internal fun ClientManager.Companion.create(
    logger: Logger,
    executor : ExecutorManager,
    spClient : SpClient
): ClientManager = ClientManagerImpl(logger, executor, spClient)

private class ClientManagerImpl(
    val logger: Logger,
    val executor : ExecutorManager,
    val spClient : SpClient
) : ClientManager {

    private var cNumber: Int = Int.MAX_VALUE
    private var storedConsent: Int = 0


    override fun setCampaignNumber(campNum: Int) {
        cNumber = campNum
        storedConsent = 0
    }

    override fun setAction(actionObj: ConsentActionImpl) {
        when (actionObj.actionType) {
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
                if (!actionObj.requestFromPm) {
                    if (cNumber > 0) cNumber--
                }

            }
        }
        checkStatus()
    }

    override fun setAction(action: NativeMessageActionType) {
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

    override fun reset() {
    }

    override fun checkStatus() {
        if (cNumber == storedConsent) {
            cNumber = Int.MAX_VALUE
            storedConsent = 0
            executor.executeOnSingleThread { spClient.onSpFinish() }
            logger.clientEvent(
                event = "onSpFinish",
                msg = "All campaigns have been processed.",
                content = ""
            )
        }
    }

    override fun storedConsent() {
        storedConsent++
    }
}
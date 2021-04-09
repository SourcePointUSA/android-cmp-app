package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.core.ExecutorManager
import java.util.*  //ktlint-disable

internal interface SpTimer {
    fun executeDelay(delay: Long, block: () -> Unit)
    fun cancel()

    companion object
}

internal fun SpTimer.Companion.create(executorManager: ExecutorManager): SpTimer = SpTimerImpl(executorManager)

private class SpTimerImpl(val executorManager: ExecutorManager) : SpTimer {

    var timer = Timer()

    override fun executeDelay(delay: Long, block: () -> Unit) {
        timer.scheduleAtFixedRate(
            object : TimerTask() {

                override fun run() {
                    executorManager.executeOnMain { block() }
                    cancel()
                }
            },
            delay, 1
        )
    }

    override fun cancel() {
        timer.cancel()
        timer = Timer()
    }
}

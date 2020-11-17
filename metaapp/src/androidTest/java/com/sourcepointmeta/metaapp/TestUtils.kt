package com.sourcepointmeta.metaapp

import kotlinx.coroutines.delay
import kotlin.jvm.Throws

@Throws(Exception::class)
suspend fun waitAndRetry(delayExecution : Long = 0, task: () -> Unit) {
    var res: TestRes.NotVerified = TestRes.NotVerified(RuntimeException("Not initialize condition!"))
    delay(delayExecution)
    repeat(30) {
        delay(400)
        when (val t = checkCondition(task)) {
            TestRes.Verified -> return
            is TestRes.NotVerified -> res = t
        }

    }
    throw res.th
}

fun checkCondition(task: () -> Unit): TestRes {
    return try {
        task()
        TestRes.Verified
    } catch (th: Throwable) {
        TestRes.NotVerified(th)
    }
}

sealed class TestRes {
    object Verified : TestRes()
    data class NotVerified(val th: Throwable) : TestRes()
}
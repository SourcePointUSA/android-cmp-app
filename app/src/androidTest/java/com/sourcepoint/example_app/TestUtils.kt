package com.sourcepoint.example_app

import kotlinx.coroutines.delay
import kotlin.jvm.Throws

@Throws(Exception::class)
suspend fun wr(delayExecution : Long = 200, task: () -> Unit) {
    var res: TestRes.NotVerified = TestRes.NotVerified(RuntimeException("Condition Not initialized!"))
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
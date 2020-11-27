package com.sourcepoint.example_app

import kotlinx.coroutines.delay
import org.junit.Assert
import kotlin.jvm.Throws

@Throws(Throwable::class)
suspend fun wr(d : Long = 200, task: () -> Unit) {
    var res: TestRes.NotVerified = TestRes.NotVerified(RuntimeException("Condition Not initialized!"))
    delay(d)
    repeat(30) {
        delay(250)
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

fun Boolean.assertTrue() = Assert.assertTrue(this)
fun Boolean.assertFalse() = Assert.assertFalse(false)
fun<T : Any?> T.assertNull() = Assert.assertNull(this)
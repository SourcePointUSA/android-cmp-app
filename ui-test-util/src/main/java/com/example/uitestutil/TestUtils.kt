package com.example.uitestutil

import kotlinx.coroutines.delay
import org.junit.Assert
import kotlin.jvm.Throws

@Throws(Throwable::class)
suspend fun wr(
    d: Long = 200,
    times: Int = 30,
    backup: (() -> Unit)? = null,
    task: () -> Unit
) {
    var res: TestRes.NotVerified = TestRes.NotVerified(RuntimeException("Condition Not initialized!"))
    delay(d)
    repeat(times) {
        delay(250)
        when (val t = checkCondition(task)) {
            TestRes.Verified -> return
            is TestRes.NotVerified -> {
                if(it % 5 == 0 && it > times / 2) {
                    backup?.invoke()
                }
                res = t
            }
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
fun <T : Any?> T.assertNull() = Assert.assertNull(this)
infix fun <T> T.assertEquals(t: T) = apply { Assert.assertEquals(t, this) }
infix fun <T> T.assertNotEquals(t: T) = apply { Assert.assertNotEquals(t, this) }
fun <T : Any?> T.assertNotNull() = apply { Assert.assertNotNull(this) }

/**
 * Receive file.json and return the content as string
 */
fun String.jsonFile2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }
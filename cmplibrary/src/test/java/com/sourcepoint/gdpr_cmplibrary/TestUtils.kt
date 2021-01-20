package com.sourcepoint.gdpr_cmplibrary

import okhttp3.Request
import okio.Buffer
import org.junit.Assert

infix fun <T> T.assertEquals(t: T) = apply { Assert.assertEquals(t, this) }
fun <T : Any?> T.assertNotNull() = apply { Assert.assertNotNull(this) }
fun <T : Any?> T.assertNull() = apply { Assert.assertNull(this) }

fun Request.readText(): String {
    val buffer = Buffer()
    this.body!!.writeTo(buffer)
    return buffer.readUtf8()
}
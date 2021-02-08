package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.util.Either
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.* // ktlint-disable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import java.lang.RuntimeException

/**
 * You need to have the local server running to test this class
 */
class ResponseManagerImplTest {

    @Test
    fun `GIVEN a response without body RETURN a Left object`() = runBlocking<Unit> {
        val sut = ResponseManager.create(JsonConverter.create())
        val resp = Response.Builder() //
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        val result = sut.parseResponse(resp) as Either.Left
        result.t.message.assertEquals("Body Response object is null")
    }

    @Test
    fun `GIVEN a response without the uuid RETURN a Left object`() = runBlocking<Unit> {
        val sut = ResponseManager.create(JsonConverter.create())
        val resp = Response.Builder() //
            .code(200)
            .body("uuid_null.json".jsonFile2String().toResponseBody("application/json".toMediaTypeOrNull()))
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        val result = sut.parseResponse(resp) as Either.Left
        result.t.message.assertEquals("uuid object is null")
    }

    @Test
    fun `GIVEN a response without message RETURN a Left object`() = runBlocking<Unit> {
        val sut = ResponseManager.create(JsonConverter.create())
        val resp = Response.Builder() //
            .code(200)
            .body("unified_wrapper/with_message_null.json".jsonFile2String().toResponseBody("application/json".toMediaTypeOrNull()))
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        val result = sut.parseResponse(resp) as Either.Left
        result.t.message!!.contains("We have [0] inst. of Message.").assertEquals(true)
    }

    @Test
    fun `GIVEN a crash RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>().also { every { it.toMessageResp(any()) }.throws(RuntimeException("test")) }
        val sut = ResponseManager.create(jsonConverter)
        val resp = Response.Builder() //
            .code(200)
            .body("unified_wrapper/with_message_null.json".jsonFile2String().toResponseBody("application/json".toMediaTypeOrNull()))
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        val result = sut.parseResponse(resp) as Either.Left
        result.t.message.assertEquals("test")
    }
}

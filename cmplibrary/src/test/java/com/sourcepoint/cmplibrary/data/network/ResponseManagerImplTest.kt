package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidRequestException
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test

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
    fun `GIVEN a crash RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>().also { every { it.toUnifiedMessageResp(any()) }.throws(RuntimeException("test")) }
        val sut = ResponseManager.create(jsonConverter)
        val resp = Response.Builder() //
            .code(200)
            .body("unified_wrapper_resp/with_message_null.json".jsonFile2String().toResponseBody("application/json".toMediaTypeOrNull()))
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        val result = sut.parseResponse(resp) as Either.Left
        result.t.message.assertEquals("test")
    }

    @Test(expected = InvalidRequestException::class)
    fun `GIVEN a 500 response code RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val sut = ResponseManager.create(jsonConverter)
        val resp = mockResponse(code = 500, message = "error", url = "https://mock.com", body = "{}")
        sut.parseConsentRes(resp, CampaignType.GDPR)
    }

    @Test(expected = java.lang.RuntimeException::class)
    fun `GIVEN a crash in jsonConverter RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>().also { every { it.toConsentResp(any(), any()) }.throws(RuntimeException("test")) }
        val sut = ResponseManager.create(jsonConverter)
        val resp = mockResponse(url = "https://mock.com", body = "{}")
        sut.parseConsentRes(resp, CampaignType.GDPR)
    }

    @Test(expected = InvalidResponseWebMessageException::class)
    fun `GIVEN a response body empty RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val sut = ResponseManager.create(jsonConverter)
        val resp = Response.Builder() //
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        sut.parseConsentRes(resp, CampaignType.GDPR)
    }

    @Test(expected = InvalidResponseWebMessageException::class)
    fun `EXECUNTING parseCustomConsentRes with a response body empty RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val sut = ResponseManager.create(jsonConverter)
        val resp = Response.Builder() //
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
        sut.parseCustomConsentRes(resp)
    }

    @Test(expected = java.lang.RuntimeException::class)
    fun `EXECUNTING parseCustomConsentRes with a crash in jsonConverter RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>().also { every { it.toCustomConsentResp(any()) }.throws(RuntimeException("test")) }
        val sut = ResponseManager.create(jsonConverter)
        val resp = mockResponse(url = "https://mock.com", body = "{}")
        sut.parseCustomConsentRes(resp)
    }

    @Test
    fun `EXECUNTING parseCustomConsentRes RETURN a Right object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val customConsentRespMock = mockk<CustomConsentResp>()
        every { jsonConverter.toCustomConsentResp(any()) }.returns(Either.Right(customConsentRespMock))
        val sut = ResponseManager.create(jsonConverter)
        val resp = mockResponse(url = "https://mock.com", body = "{}")
        val res = sut.parseCustomConsentRes(resp)
        res.assertEquals(customConsentRespMock)
    }
}

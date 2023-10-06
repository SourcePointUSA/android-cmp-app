package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.stub.MockLogger
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Test

/**
 * You need to have the local server running to test this class
 */
class ResponseManagerImplTest {

    @Test(expected = java.lang.RuntimeException::class)
    fun `EXECUNTING parseCustomConsentRes with a response body empty RETURN a Left object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val sut = ResponseManager.create(jsonConverter, MockLogger)
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
        val sut = ResponseManager.create(jsonConverter, MockLogger)
        val resp = mockResponse(url = "https://mock.com", body = "{}")
        sut.parseCustomConsentRes(resp)
    }

    @Test
    fun `EXECUNTING parseCustomConsentRes RETURN a Right object`() = runBlocking<Unit> {
        val jsonConverter = mockk<JsonConverter>()
        val customConsentRespMock = mockk<CustomConsentResp>()
        every { jsonConverter.toCustomConsentResp(any()) }.returns(Either.Right(customConsentRespMock))
        val sut = ResponseManager.create(jsonConverter, MockLogger)
        val resp = mockResponse(url = "https://mock.com", body = "{}")
        val res = sut.parseCustomConsentRes(resp)
        res.assertEquals(customConsentRespMock)
    }
}

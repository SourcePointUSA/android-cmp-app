package com.sourcepoint.gdpr_cmplibrary.exception

import com.example.gdpr_cmplibrary.BuildConfig
import com.sourcepoint.gdpr_cmplibrary.assertEquals
import com.sourcepoint.gdpr_cmplibrary.readText
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Test

class LoggerImplTest {

    @MockK
    private lateinit var messageManager: ErrorMessageManager

    @MockK
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a NoInternetConnectionException VERIFY the generated call`() {
        val ex = InvalidResponseWebMessageException(cause = null, description = "description")
        val json = json(CodeList.INVALID_RESPONSE_WEB_MESSAGE)
        every { messageManager.build(ex) } returns json

        val cb: (String, String) -> Unit = { _, _ -> }
        val sut = createLogger4Testing(cb, cb, cb, client, messageManager, "https://myserver.com/")
        sut.error(ex)

        /** We have 2 different implementation for Debug and Release */
        when (BuildConfig.DEBUG) {
            true -> verify(exactly = 0) { client.newCall(any()) }
            false -> {
                val slot = slot<Request>()
                verify(exactly = 1) { client.newCall(capture(slot)) }
                slot.captured.run {
                    readText().assertEquals(json)
                    url.toString().assertEquals("https://myserver.com/")
                    method.assertEquals("POST")
                }
            }
        }
    }

    @Test
    fun `GIVEN a RenderingAppException VERIFY the generated call`() {
        val ex = RenderingAppException(cause = null, description = "description", pCode = "custom_code")
        val json = json(ExceptionCodes("custom_code"))
        every { messageManager.build(ex) } returns json

        val cb: (String, String) -> Unit = { _, _ -> }
        val sut = createLogger4Testing(cb, cb, cb, client, messageManager, "https://myserver.com/")
        sut.error(ex)

        /** We have 2 different implementation for Debug and Release */
        when (BuildConfig.DEBUG) {
            true -> verify(exactly = 0) { client.newCall(any()) }
            false -> {
                val slot = slot<Request>()
                verify(exactly = 1) { client.newCall(capture(slot)) }
                slot.captured.run {
                    readText().assertEquals(json)
                    url.toString().assertEquals("https://myserver.com/")
                    method.assertEquals("POST")
                }
            }
        }
    }

    private fun json(errorCode: ExceptionCodes) = """
            {
                "code" : "${errorCode.code}",
                "accountId" : "accountId",
                "propertyHref" : "https://ref.com",
                "propertyId" : "propertyId",
                "description" : "description"
                "clientVersion" : "clientVersion}",
                "OSVersion" : "osVersion",
                "deviceFamily" : "deviceFamily",
                "legislation" : "legislation"
            }
        """.trimIndent()
}
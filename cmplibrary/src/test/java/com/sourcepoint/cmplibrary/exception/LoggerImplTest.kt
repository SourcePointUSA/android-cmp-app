package com.sourcepoint.cmplibrary.exception

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.readText
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
            true -> {
                val slot = slot<Request>()
                verify(exactly = 1) { client.newCall(capture(slot)) }
                slot.captured.run {
                    readText().assertEquals(json)
                    url.toString().assertEquals("https://myserver.com/?scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
                    method.assertEquals("POST")
                }
            }
            else -> {
                // we had to implement both cases, should leave this case empty
            }
        }
    }

    @Test
    fun `GIVEN a RenderingAppException VERIFY the generated call`() {
        val ex = RenderingAppException(cause = null, description = "description")
        val json = json(ExceptionCodes("custom_code"))
        every { messageManager.build(ex) } returns json

        val cb: (String, String) -> Unit = { _, _ -> }
        val sut = createLogger4Testing(cb, cb, cb, client, messageManager, "https://myserver.com/")
        sut.error(ex)

        /** We have 2 different implementation for Debug and Release */
        when (BuildConfig.DEBUG) {
            true -> {
                val slot = slot<Request>()
                verify(exactly = 1) { client.newCall(capture(slot)) }
                slot.captured.run {
                    readText().assertEquals(json)
                    url.toString().assertEquals("https://myserver.com/?scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
                    method.assertEquals("POST")
                }
            }
            else -> {
                // we had to implement both cases, should leave this case empty
            }
        }
    }

    private fun json(errorCode: ExceptionCodes) = """
            {
                "code" : "${errorCode.errorCode}",
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

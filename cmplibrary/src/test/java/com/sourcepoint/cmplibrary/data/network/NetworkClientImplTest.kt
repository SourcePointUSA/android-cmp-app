package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.CodeList
import com.sourcepoint.cmplibrary.exception.ConnectionTimeoutException
import com.sourcepoint.cmplibrary.stub.MockLogger
import com.sourcepoint.mobile_core.models.SPNetworkError
import io.mockk.* // ktlint-disable
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.* // ktlint-disable
import okhttp3.Call
import okhttp3.OkHttpClient
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.InterruptedIOException

class NetworkClientImplTest {

    @MockK
    lateinit var okHttp: OkHttpClient

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    @MockK
    private lateinit var responseManager: ResponseManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    private val sut by lazy {
        createNetworkClient(
            accountId = 123,
            propertyId = 123,
            propertyName = "",
            httpClient = okHttp,
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = MockLogger
        )
    }

    @Test
    fun `EXECUTE deleteCustomConsentTo THROWS an exception with okHttp`() {
        assertThrows(
            SPNetworkError::class.java
        ) {
            sut.deleteCustomConsentTo(
                "uuid",
                1,
                listOf("a"),
                listOf("b"),
                listOf()
            )
        }
    }

    @Test
    fun `EXECUTE sendCustomConsent THROWS an exception with okHttp`() {
        assertThrows(
            SPNetworkError::class.java
        ) {
            sut.sendCustomConsent(
                "uuid",
                1,
                listOf("a"),
                listOf("b"),
                listOf()
            )
        }
    }

    @Test
    fun `EXECUTE getMessages THROWS an InterruptedIOException and the result is a LEFT obj`() {
        val mockCall = mockk<Call>()

        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(InterruptedIOException("exception"))

        val param = MessagesParamReq(
            accountId = 1,
            propertyId = 1,
            authId = null,
            propertyHref = "prop",
            env = Env.LOCAL_PROD,
            metadataArg = null,
            body = "{}",
        )

        val res = sut.getMessages(param) as? Either.Left
        (res!!.t as ConnectionTimeoutException).code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + ApiRequestPostfix.MESSAGES.apiPostfix)
    }
}

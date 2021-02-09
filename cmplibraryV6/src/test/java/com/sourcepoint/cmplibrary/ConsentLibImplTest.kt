package com.sourcepoint.cmplibrary

import android.content.Context
import com.sourcepoint.cmplibrary.core.layout.NativeMessage
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.MissingClientException
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.cmplibrary.stub.MockService
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.file2String
import io.mockk.*  //ktlint-disable
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class ConsentLibImplTest {

    var campaign = Campaign(22, 7639, "tcfv2.mobile.webview", "122058")
    @MockK
    private lateinit var appCtx: Context
    @MockK
    private lateinit var logger: Logger
    @MockK
    private lateinit var jsonConverter: JsonConverter
    @MockK
    private lateinit var connManager: ConnectionManager
    @MockK
    private lateinit var viewManager: ViewsManager
    @MockK
    private lateinit var execManager: ExecutorManager
    @MockK
    private lateinit var spClient: SpClient
    @MockK
    private lateinit var urlManager: HttpUrlManager
    @MockK
    private lateinit var service: Service

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test(expected = MissingClientException::class)
    fun `CALLING loadMessage() with a null SpGDPRClient THROWS a MissingClientException`() {
        val sut = ConsentLibImpl(urlManager, campaign, PrivacyManagerTabK.FEATURES, appCtx, logger, jsonConverter, connManager, service, viewManager, execManager)
        sut.loadMessage()
        verify(exactly = 1) { service.getMessage(any(), any(), any()) }
    }

    @Test
    fun `CALLING loadMessage() verify that getMessage is called exactly 1 time`() {
        val sut = ConsentLibImpl(urlManager, campaign, PrivacyManagerTabK.FEATURES, appCtx, logger, jsonConverter, connManager, service, viewManager, execManager)
        sut.spClient = spClient
        sut.loadMessage()
        verify(exactly = 1) { service.getMessage(any(), any(), any()) }
    }

    @Test
    fun `CALLING loadMessage() with verify that spClient is called`() = runBlocking<Unit> {
        val mr = MessageResp(legislation = Legislation.GDPR, message = JSONObject(), uuid = "", meta = "", userConsent = mockk())
        val mockService = MockService(
            getMessageLogic = { _, pSuccess, _ -> pSuccess.invoke(mr) }
        )
        val sut = ConsentLibImpl(urlManager, campaign, PrivacyManagerTabK.FEATURES, appCtx, logger, jsonConverter, connManager, mockService, viewManager, execManager)
        sut.spClient = spClient

        sut.loadMessage()

//        val slot = slot<ActionTypes>()
//        verify(exactly = 1) { spClient.onAction(capture(slot)) }
//        slot.captured
    }

    // TODO
    @Test
    fun `CALLING loadNativeMessage() with verify that spClient is called`() = runBlocking<Unit> {
        val dtJson = "msgJSON.json".file2String()
        val mockService = MockService(
            getNativeMessageLogic = { _, pSuccess, _ -> pSuccess.invoke(NativeMessageResp(JSONObject(dtJson))) }
        )
        val sut = ConsentLibImpl(urlManager, campaign, PrivacyManagerTabK.FEATURES, appCtx, logger, jsonConverter, connManager, mockService, viewManager, MockExecutorManager())
        sut.spClient = spClient

        val nm = mockk<NativeMessage>()
        every { nm.setAttributes(any()) }.returns(Unit)
        every { nm.setActionClient(any()) }.returns(Unit)
        sut.loadMessage(nm)

        val slot = slot<NativeMessage>()
        verify(exactly = 1) { spClient.onConsentUIReady(capture(slot)) }
        slot.captured
    }
}

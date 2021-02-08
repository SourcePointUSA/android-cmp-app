package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.exception.GenericSDKException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.toMessageReq
import com.sourcepoint.cmplibrary.stub.MockNetworkClient
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class ServiceImplTest {

    @MockK
    private lateinit var ds: DataStorage

    @MockK
    private lateinit var successMock: (MessageResp) -> Unit

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyId = 7094,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a success from NetworkClient VERIFY that saveAppliedLegislation is called`() {
        val mr = MessageResp(legislation = Legislation.GDPR, message = JSONObject(), uuid = "", meta = "", userConsent = mockk())
        val nc = MockNetworkClient(
            logicMess = { _, success, _ -> success(mr) }
        )
        every { successMock(any()) }.answers { }

        val sut = Service.create(nc, ds)
        sut.getMessage(nativeCampaign.toMessageReq(), successMock, errorMock)

        verify(exactly = 1) { ds.saveAppliedLegislation(Legislation.GDPR.name) }
        verify(exactly = 1) { successMock(any()) }
        verify(exactly = 0) { errorMock(any()) }
    }

    @Test
    fun `GIVEN an error from NetworkClient VERIFY that saveAppliedLegislation is NOT called`() {
        val nc = MockNetworkClient(
            logicMess = { _, _, error -> error(GenericSDKException(description = "tests")) }
        )
        every { errorMock(any()) }.answers { }

        val sut = Service.create(nc, ds)
        sut.getMessage(nativeCampaign.toMessageReq(), successMock, errorMock)

        verify(exactly = 0) { ds.saveAppliedLegislation(Legislation.GDPR.name) }
        verify(exactly = 0) { successMock(any()) }
        verify(exactly = 1) { errorMock(any()) }
    }
}

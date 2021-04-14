package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.model.Campaign
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class ServiceImplTest {

    @MockK
    private lateinit var ds: DataStorage

//    @MockK
//    private lateinit var umr: UnifiedMessageResp

//    @MockK
//    private lateinit var successMock: (UnifiedMessageResp) -> Unit
//
//    @MockK
//    private lateinit var errorMock: (Throwable) -> Unit

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a success from NetworkClient VERIFY that saveAppliedLegislation is called`() {
//        val mr = MessageResp(legislation = Legislation.GDPR, message = JSONObject(), uuid = "", meta = "", spUserConsent = mockk())
//        val nc = MockNetworkClient(
//            logicUnifiedMess = { _, success, _ -> success(umr) }
//        )
//        every { successMock(any()) }.answers { }
//
//        val sut = Service.create(nc, ds)
//        sut.getMessage(nativeCampaign.toMessageReq(), successMock, errorMock)
//
// //        verify(exactly = 1) { ds.saveAppliedLegislation(Legislation.GDPR.name) }
//        verify(exactly = 1) { successMock(any()) }
//        verify(exactly = 0) { errorMock(any()) }
    }

//    @Test
//    fun `GIVEN an error from NetworkClient VERIFY that saveAppliedLegislation is NOT called`() {
//        val nc = MockNetworkClient(
//            logicUnifiedMess = { _, _, error -> error(GenericSDKException(description = "tests")) }
//        )
//        every { errorMock(any()) }.answers { }
//
//        val sut = Service.create(nc, ds)
//        sut.getMessage(nativeCampaign.toMessageReqMock(), successMock, errorMock)
//
//        verify(exactly = 0) { ds.saveAppliedLegislation(Legislation.GDPR.name) }
//        verify(exactly = 0) { successMock(any()) }
//        verify(exactly = 1) { errorMock(any()) }
//    }
}

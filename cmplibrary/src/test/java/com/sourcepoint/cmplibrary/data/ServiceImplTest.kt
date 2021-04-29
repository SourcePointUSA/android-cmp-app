package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.GenericSDKException
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.*
import com.sourcepoint.cmplibrary.stub.MockNetworkClient
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.uwMessDataTest
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
    private lateinit var ncMock: NetworkClient

    @MockK
    private lateinit var ds: DataStorage

    @MockK
    private lateinit var cm: CampaignManager

    @MockK
    private lateinit var cmu: ConsentManagerUtils

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var successMock: (UnifiedMessageResp) -> Unit

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

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
    fun `GIVEN a success from NetworkClient VERIFY that saveUnifiedMessageResp is called`() {
        val umr = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        val nc = MockNetworkClient(
            logicUnifiedMess = { _, success, _ -> success(umr) }
        )
        every { successMock(any()) }.answers { }

        val sut = Service.create(nc, cm, cmu, ds, logger)
        sut.getUnifiedMessage(uwMessDataTest, successMock, errorMock, Env.STAGE)

        verify(exactly = 1) { cm.saveUnifiedMessageResp(any()) }
        verify(exactly = 1) { successMock(any()) }
        verify(exactly = 0) { errorMock(any()) }
    }

    @Test
    fun `GIVEN an error from NetworkClient VERIFY that saveUnifiedMessageResp is NOT called`() {
        val nc = MockNetworkClient(
            logicUnifiedMess = { _, _, localError -> localError(GenericSDKException(description = "tests")) }
        )

        every { errorMock(any()) }.answers { }

        val sut = Service.create(nc, cm, cmu, ds, logger)
        sut.getUnifiedMessage(uwMessDataTest, successMock, errorMock, Env.STAGE)

        verify(exactly = 0) { cm.saveUnifiedMessageResp(any()) }
        verify(exactly = 0) { successMock(any()) }
        verify(exactly = 1) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a custom consent UPDATE the stored consent`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger)
        val res = sut.sendCustomConsent(mockk(), Env.STAGE).getOrNull()!!
        res.content.getJSONObject("grants").toTreeMap().assertEquals(JSONObject(newConsent).getJSONObject("grants").toTreeMap())
    }
}

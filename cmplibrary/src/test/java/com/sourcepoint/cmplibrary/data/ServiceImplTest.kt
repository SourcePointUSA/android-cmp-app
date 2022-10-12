package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.* //ktlint-disable
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.v7.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.GenericSDKException
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.messagesParamReq
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.stub.MockDataStorage
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.cmplibrary.stub.MockNetworkClient
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.uwMessDataTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.time.temporal.ChronoUnit

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
    private lateinit var execManager: ExecutorManager

    @MockK
    private lateinit var successMock: (UnifiedMessageResp) -> Unit

    @MockK
    private lateinit var successMockV7: (MessagesResp) -> Unit

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

        val sut = Service.create(nc, cm, cmu, ds, logger, execManager)
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

        val sut = Service.create(nc, cm, cmu, ds, logger, execManager)
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

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendCustomConsent(mockk(), Env.STAGE).getOrNull()!!
        res.content.getJSONObject("grants").toTreeMap()
            .assertEquals(JSONObject(newConsent).getJSONObject("grants").toTreeMap())
    }

    @Test
    fun `GIVEN a GDPR consent UPDATE the stored consent`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()
        val consentAction = ConsentActionImpl(
            requestFromPm = false,
            campaignType = CampaignType.GDPR,
            actionType = ActionType.ACCEPT_ALL,
            choiceId = "123",
            privacyManagerId = "100",
            pmTab = "default"
        )

        every { ncMock.sendConsent(any(), any(), any()) }.returns(
            Right(
                ConsentResp(
                    JSONObject(newConsent),
                    "userConsent",
                    "123",
                    "localstate",
                    CampaignType.GDPR
                )
            )
        )
        every { cmu.buildConsentReq(any(), any(), any()) }.returns(Right(JSONObject()))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendConsent(
            localState = "{}",
            pmId = null,
            env = Env.STAGE,
            consentActionImpl = consentAction
        ) as? Right

        verify(exactly = 1) { ds.saveLocalState("localstate") }
        verify(exactly = 1) { ds.saveGdprConsentResp("userConsent") }
        verify(exactly = 1) { ds.saveGdprConsentUuid("123") }
        verify(exactly = 0) { ds.saveCcpaConsentResp(any()) }
        verify(exactly = 0) { ds.saveCcpaConsentUuid(any()) }

        res.assertNotNull()
    }

    @Test
    fun `GIVEN a CCPA consent UPDATE the stored consent`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()
        val consentAction = ConsentActionImpl(
            requestFromPm = false,
            campaignType = CampaignType.CCPA,
            actionType = ActionType.ACCEPT_ALL,
            choiceId = "123",
            privacyManagerId = "100",
            pmTab = "default"
        )

        every { ncMock.sendConsent(any(), any(), any()) }.returns(
            Right(
                ConsentResp(
                    JSONObject(newConsent),
                    "userConsent",
                    "123",
                    "localstate",
                    CampaignType.CCPA
                )
            )
        )
        every { cmu.buildConsentReq(any(), any(), any()) }.returns(Right(JSONObject()))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendConsent(
            localState = "{}",
            pmId = null,
            env = Env.STAGE,
            consentActionImpl = consentAction
        ) as? Right

        verify(exactly = 1) { ds.saveLocalState("localstate") }
        verify(exactly = 1) { ds.saveCcpaConsentResp("userConsent") }
        verify(exactly = 1) { ds.saveCcpaConsentUuid("123") }
        verify(exactly = 0) { ds.saveGdprConsentResp(any()) }
        verify(exactly = 0) { ds.saveGdprConsentUuid(any()) }

        res.assertNotNull()
    }

    @Test
    fun `GIVEN a CCPA-GDPR consent RETURN a Left obj`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()
        val consentAction = ConsentActionImpl(
            requestFromPm = false,
            campaignType = CampaignType.CCPA,
            actionType = ActionType.ACCEPT_ALL,
            choiceId = "123",
            privacyManagerId = "100",
            pmTab = "default"
        )

        every { ncMock.sendConsent(any(), any(), any()) }.returns(
            Right(
                ConsentResp(
                    JSONObject(newConsent),
                    "userConsent",
                    "123",
                    "localstate",
                    CampaignType.CCPA
                )
            )
        )
        every { cmu.buildConsentReq(any(), any(), any()) }.returns(Right(JSONObject()))
        every { ds.getGdprConsentResp() }.returns(storedConsent)
        every { ds.saveLocalState(any()) }.throws(RuntimeException("test"))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendConsent(
            localState = "{}",
            pmId = null,
            env = Env.STAGE,
            consentActionImpl = consentAction
        ) as? Either.Left

        verify(exactly = 1) { ds.saveLocalState(any()) }
        verify(exactly = 0) { ds.saveCcpaConsentResp(any()) }
        verify(exactly = 0) { ds.saveCcpaConsentUuid(any()) }
        verify(exactly = 0) { ds.saveGdprConsentResp(any()) }
        verify(exactly = 0) { ds.saveGdprConsentUuid(any()) }

        res.assertNotNull()
    }

    @Test
    fun `GIVEN a custom consent VERIFY that the data storage is called`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE).getOrNull()!!

        verify(exactly = 1) { ds.saveGdprConsentResp(any()) }

        res.gdpr.assertNotNull()
        res.ccpa.assertNotNull()
    }

    @Test
    fun `GIVEN a deleted custom consent VERIFY that the data storage is called`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.deleteCustomConsentToServ(mockk(), Env.STAGE).getOrNull()!!

        verify(exactly = 1) { ds.saveGdprConsentResp(any()) }

        res.gdpr.assertNotNull()
        res.ccpa.assertNotNull()
    }

    @Test
    fun `GIVEN a custom consent VERIFY that the grants are updated`() {
        val dsStub = MockDataStorage()
        // initial saved consent
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        dsStub.saveGdprConsentResp(storedConsent)
        // new custom consent result
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))

        val sut = Service.create(ncMock, cm, cmu, dsStub, logger, execManager)
        sut.sendCustomConsentServ(mockk(), Env.STAGE).getOrNull()!!

        // compare that the new consent get stored
        val customStoredGrants = JSONObject(dsStub.getGdprConsentResp()).toTreeMap().getMap("grants")!!
        val customGrants = JSONObject(newConsent).toTreeMap().getMap("grants")!!
        customGrants.assertEquals(customStoredGrants)
    }

    @Test
    fun `GIVEN a deleted custom consent VERIFY that the grants are updated`() {
        val dsStub = MockDataStorage()
        // initial saved consent
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        dsStub.saveGdprConsentResp(storedConsent)
        // new custom consent result
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))

        val sut = Service.create(ncMock, cm, cmu, dsStub, logger, execManager)
        sut.deleteCustomConsentToServ(mockk(), Env.STAGE).getOrNull()!!

        // compare that the new consent get stored
        val customStoredGrants = JSONObject(dsStub.getGdprConsentResp()).toTreeMap().getMap("grants")!!
        val customGrants = JSONObject(newConsent).toTreeMap().getMap("grants")!!
        customGrants.assertEquals(customStoredGrants)
    }

    @Test
    fun `GIVEN a custom consent THROWS an exception`() {
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.throws(RuntimeException("test"))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE)
        (res as? Either.Left).assertNotNull()
    }

    @Test
    fun `GIVEN a deleted custom consent THROWS an exception`() {
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.throws(RuntimeException("test"))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE)
        (res as? Either.Left).assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun `GIVEN a Left object during a MetaData throw an exception`() {

        every { ncMock.getMetaData(any()) }.returns(Either.Left(RuntimeException()))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messagesParamReq, successMockV7, errorMock)

        verify(exactly = 1) { errorMock(any()) }
        verify(exactly = 0) { successMockV7(any()) }
    }

    @Test
    fun `GIVEN a MetaData resp VERIFY that the consentStatus call is called`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        every { ncMock.getMetaData(any()) }.returns(Either.Right(metadata))
        every { cm.consentStatusResponse }.returns(null)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messagesParamReq, successMockV7, errorMock)

//        verify(exactly = 0) { errorMock(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any()) }
    }

    @Test
    fun `GIVEN a saved ConsentStatus resp VERIFY that the dates are saved`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        val consentStatusJson = "v7/consent_status_with_auth_id.json".file2String()
        val consentStatus = JsonConverter.converter.decodeFromString<ConsentStatusResp>(consentStatusJson)

        val messageJson = "v7/messagesObj.json".file2String()
        val messageResp = JsonConverter.converter.decodeFromString<MessagesResp>(messageJson)

        val gdprConsentStatus: ConsentStatus = JsonConverter.converter.decodeFromString(
            """
              {
                "rejectedAny": true,
                "rejectedLI": false,
                "consentedAll": false,
                "granularStatus": {
                  "vendorConsent": "NONE",
                  "vendorLegInt": "ALL",
                  "purposeConsent": "NONE",
                  "purposeLegInt": "ALL",
                  "previousOptInAll": false,
                  "defaultConsent": true
                },
                "hasConsentData": false,
                "consentedToAny": false
              } 
            """.trimIndent()
        )

        every { ncMock.getMetaData(any()) }.returns(Right(metadata))
        every { cm.consentStatusResponse }.returns(consentStatus)
        every { cm.dataRecordedConsent }.returns(consentStatus.consentStatusData!!.gdpr!!.dateCreated)
        every { cm.gdprConsentStatus }.returns(gdprConsentStatus)
        every { cm.dataRecordedConsent }.returns(
            consentStatus.consentStatusData!!.gdpr!!.dateCreated!!.minus(
                400,
                ChronoUnit.DAYS
            )
        )

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messagesParamReq, successMockV7, errorMock)

        verify {
            cm.gdprConsentStatus = withArg {
                it.granularStatus?.previousOptInAll!!.assertTrue()
                it.legalBasisChanges!!.assertTrue()
                it.vendorListAdditions!!.assertTrue()
            }
        }
    }
    @Test
    fun `GIVEN a saved ConsentStatus resp VERIFY that the dates are saved 2`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        val consentStatusJson = "v7/consent_status_with_auth_id.json".file2String()
        val consentStatus = JsonConverter.converter.decodeFromString<ConsentStatusResp>(consentStatusJson)

        val messageJson = "v7/messagesObj.json".file2String()
        val messageResp = JsonConverter.converter.decodeFromString<MessagesResp>(messageJson)

        val gdprConsentStatus: ConsentStatus = JsonConverter.converter.decodeFromString(
            """
              {
                "rejectedAny": true,
                "rejectedLI": false,
                "consentedAll": false,
                "granularStatus": {
                  "vendorConsent": "NONE",
                  "vendorLegInt": "ALL",
                  "purposeConsent": "NONE",
                  "purposeLegInt": "ALL",
                  "previousOptInAll": false,
                  "defaultConsent": true
                },
                "hasConsentData": false,
                "consentedToAny": false
              } 
            """.trimIndent()
        )

        every { ncMock.getMetaData(any()) }.returns(Right(metadata))
        every { cm.consentStatusResponse }.returns(consentStatus)
        every { cm.dataRecordedConsent }.returns(consentStatus.consentStatusData!!.gdpr!!.dateCreated)
        every { cm.gdprConsentStatus }.returns(gdprConsentStatus)
        every { cm.dataRecordedConsent }.returns(
            consentStatus.consentStatusData!!.gdpr!!.dateCreated!!
        )

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messagesParamReq, successMockV7, errorMock)

        verify {
            cm.gdprConsentStatus = withArg {
                it.granularStatus?.previousOptInAll!!.assertFalse()
                it.legalBasisChanges.assertNull()
                it.vendorListAdditions.assertNull()
            }
        }
    }
}

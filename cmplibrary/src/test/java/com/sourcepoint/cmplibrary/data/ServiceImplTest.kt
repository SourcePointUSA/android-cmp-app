package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.messagesParamReq
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.stub.MockDataStorage
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.cmplibrary.util.file2String
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
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
    private lateinit var mockMetaDataResp: MetaDataResp

    @MockK
    private lateinit var mockConsentStatusResp: ConsentStatusResp

    @MockK
    private lateinit var execManager: ExecutorManager

    @MockK
    private lateinit var successMock: (UnifiedMessageResp) -> Unit

    @MockK
    private lateinit var successMockV7: (MessagesResp) -> Unit

    @MockK
    private lateinit var consentMockV7: () -> Unit

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    private val spConfig = SpConfig(
        22,
        "asfa",
        emptyList(),
        MessageLanguage.ENGLISH,
        propertyId = 1234,
        messageTimeout = 3000,
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
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
        (res as? Left).assertNotNull()
    }

    @Test
    fun `GIVEN a deleted custom consent THROWS an exception`() {
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.throws(RuntimeException("test"))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE)
        (res as? Left).assertNotNull()
    }

    @Test
    fun `GIVEN a MetaData resp VERIFY that the consentStatus call is executed`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        every { ncMock.getMetaData(any()) }.returns(Either.Right(metadata))
        every { cm.shouldCallConsentStatus }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messageReq = messagesParamReq, showConsent = consentMockV7, pSuccess = successMockV7, pError = errorMock)

//        verify(exactly = 0) { errorMock(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any()) }
    }

    @Test
    fun `GIVEN a consentStatus resp VERIFY that the consentStatus is saved`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        val consentStatusJson = "v7/consent_status_with_auth_id.json".file2String()
        val consentStatus = JsonConverter.converter.decodeFromString<ConsentStatusResp>(consentStatusJson)

        every { ncMock.getMetaData(any()) }.returns(Either.Right(metadata))
        every { ncMock.getConsentStatus(any()) }.returns(Either.Right(consentStatus))
        every { cm.shouldCallConsentStatus }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(messageReq = messagesParamReq, showConsent = consentMockV7, pSuccess = successMockV7, pError = errorMock)

        // TODO
    }

    @Test
    fun `GIVEN a Left during getMetaData req RETURN call the error callback`() {

        val messageJson = "v7/messagesObj.json".file2String()
        val messageResp = JsonConverter.converter.decodeFromString<MessagesResp>(messageJson)

        every { ncMock.getMetaData(any()) }.returns(Left(RuntimeException()))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            pSuccess = successMockV7,
            pError = errorMock
        )

        verify(exactly = 1) { errorMock(any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }

    @Test
    fun `GIVEN a Left during getMetaData req CALL onError`() {

        every { ncMock.getMetaData(any()) }.returns(Right(mockMetaDataResp))
        every { ncMock.getConsentStatus(any()) }.returns(Left(RuntimeException()))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(
            messageReq = messagesParamReq.copy(authId = "test"),
            showConsent = consentMockV7,
            pSuccess = successMockV7,
            pError = errorMock
        )

        verify(exactly = 1) { errorMock(any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }

    @Test
    fun `GIVEN a Left object during the getConsentStatus req CALL the error cb`() {

        every { ncMock.getMetaData(any()) }.returns(Right(mockMetaDataResp))
        every { ncMock.getConsentStatus(any()) }.returns(Right(mockConsentStatusResp))
        every { ncMock.getMessages(any()) }.returns(Left(RuntimeException()))
        every { cm.shouldCallMessages }.returns(true)
        every { cm.messagesOptimizedLocalState }.returns(JsonObject(emptyMap()))
        every { cm.campaigns4Config }.returns(emptyList())

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager())
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            pSuccess = successMockV7,
            pError = errorMock
        )

        verify(exactly = 1) { errorMock(any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }
}

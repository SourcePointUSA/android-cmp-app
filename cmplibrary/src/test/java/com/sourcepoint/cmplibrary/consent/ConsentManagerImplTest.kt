package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class ConsentManagerImplTest {

    @MockK
    private lateinit var sPSuccessMock: ((SPConsents) -> Unit)

    @MockK
    private lateinit var sPErrorMock: ((Throwable) -> Unit)

    @MockK
    private lateinit var clientEventManager: ClientEventManager

    @MockK
    private lateinit var service: Service

    @MockK
    private lateinit var consentManagerUtils: ConsentManagerUtils

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var dataStorage: DataStorage

    private val consentResp = ConsentResp(
        uuid = "uuid_test",
        localState = "localState_test",
        campaignType = CampaignType.GDPR,
        userConsent = "{}",
        content = JSONObject()
    )

    private val consentAction = ConsentActionImpl(
        requestFromPm = false,
        campaignType = CampaignType.GDPR,
        actionType = ActionType.ACCEPT_ALL,
        choiceId = "123",
        privacyManagerId = "100",
        pmTab = "default"
    )

    private val consentManager by lazy {
        ConsentManager.create(
            service = service,
            consentManagerUtils = consentManagerUtils,
            env = Env.PROD,
            logger = logger,
            dataStorage = dataStorage,
            executorManager = MockExecutorManager(),
            clientEventManager = clientEventManager
        )
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a GDPR stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.gdprConsentStatus }.returns("gdpr")
        every { dataStorage.ccpaConsentStatus }.returns(null)
        consentManager.storedConsent.assertTrue()
    }

    @Test
    fun `GIVEN a GDPR and CCPA stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.gdprConsentStatus }.returns("gdpr")
        every { dataStorage.ccpaConsentStatus }.returns("ccpa")
        consentManager.storedConsent.assertTrue()
    }

    @Test
    fun `GIVEN a CCPA stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.ccpaConsentStatus }.returns("ccpa")
        every { dataStorage.gdprConsentStatus }.returns(null)
        consentManager.storedConsent.assertTrue()
    }
}

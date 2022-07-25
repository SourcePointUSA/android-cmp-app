package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.stub.MockDataStorage
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

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

    private var dataStorageMock: DataStorage = MockDataStorage()

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
    fun `GIVEN only a localState VERIFY that sendConsent is Not invoked`() {
        consentManager.sPConsentsSuccess = sPSuccessMock
        consentManager.sPConsentsError = sPErrorMock

        consentManager.localStateStatus = LocalStateStatus.Present("localStateTest")

        verify(exactly = 0) { service.sendConsent(any(), any(), any()) }
        verify(exactly = 0) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and an action VERIFY that sendConsent is Not invoked`() {

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        consentManager.enqueueConsent(consentAction)

        verify(exactly = 1) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 1) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsSuccess is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        repeat(times) { consentManager.enqueueConsent(consentAction) }

        verify(exactly = times) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = times) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsError is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        repeat(times) { consentManager.enqueueConsent(consentAction) }

        verify(exactly = times) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 0) { sPSuccessMock.invoke(any()) }
        verify(exactly = times) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN three actions executed from different threads and a localState VERIFY that sPConsentsSuccess is invoked three times`() = runBlocking<Unit> {

        class RandomExecutorManager : ExecutorManager by MockExecutorManager() {

            override fun executeOnSingleThread(block: () -> Unit) {
                Thread.sleep(Random.nextLong(500))
                block()
            }
        }

        val re = RandomExecutorManager()

        val consentManager = ConsentManager.create(
            service = service,
            consentManagerUtils = consentManagerUtils,
            env = Env.PROD,
            logger = logger,
            dataStorage = dataStorageMock,
            executorManager = re,
            clientEventManager = clientEventManager
        )

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        val job = launch {
            launch { consentManager.enqueueConsent(consentAction) }
            launch { consentManager.enqueueConsent(consentAction) }
            launch { consentManager.enqueueConsent(consentAction) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(3)
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 3) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState three actions executed from different threads VERIFY that sPConsentsSuccess is invoked three times`() = runBlocking<Unit> {

        class RandomExecutorManager : ExecutorManager by MockExecutorManager() {

            override fun executeOnSingleThread(block: () -> Unit) {
                Thread.sleep(Random.nextLong(500))
                block()
            }
        }

        val re = RandomExecutorManager()

        val consentManager = ConsentManager.create(
            service = service,
            consentManagerUtils = consentManagerUtils,
            env = Env.PROD,
            logger = logger,
            dataStorage = dataStorage,
            executorManager = re,
            clientEventManager = clientEventManager
        )

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        val job = launch {
            launch { consentManager.enqueueConsent(consentAction) }
            launch { consentManager.enqueueConsent(consentAction) }
            launch { consentManager.enqueueConsent(consentAction) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 3) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a GDPR stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.getGdprConsentResp() }.returns("gdpr")
        every { dataStorage.getCcpaConsentResp() }.returns(null)
        consentManager.storedConsentByUser.assertTrue()
    }

    @Test
    fun `GIVEN a GDPR and CCPA stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.getGdprConsentResp() }.returns("gdpr")
        every { dataStorage.getCcpaConsentResp() }.returns("ccpa")
        consentManager.storedConsentByUser.assertTrue()
    }

    @Test
    fun `GIVEN a CCPA stored consent in SP VERIFY that savedConsentByUser RETURN true`() {
        every { dataStorage.getCcpaConsentResp() }.returns("ccpa")
        every { dataStorage.getGdprConsentResp() }.returns(null)
        consentManager.storedConsentByUser.assertTrue()
    }

    @Test
    fun `GIVEN a MISSING stored consent in SP VERIFY that savedConsentByUser RETURN false`() {
        every { dataStorage.getGdprConsentResp() }.returns(null)
        every { dataStorage.getCcpaConsentResp() }.returns(null)
        consentManager.storedConsentByUser.assertFalse()
    }
}

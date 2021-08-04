package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
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
    private lateinit var sPSuccessMock: ((SPConsents, fromPm: Boolean) -> Unit)

    @MockK
    private lateinit var sPErrorMock: ((Throwable) -> Unit)

    @Mock
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

    private val consentAction = ConsentAction(
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
            executorManager = MockExecutorManager()
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

        consentManager.localStateStatus = LocalStateStatus.Present("localStateTest", false)

        verify(exactly = 0) { service.sendConsent(any(), any(), any()) }
        verify(exactly = 0) { sPSuccessMock.invoke(any(), false) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and an action VERIFY that sendConsent is Not invoked`() {

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents, fromPm -> sPSuccessMock(spConsents, fromPm) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test", false)
        consentManager.enqueueConsent(consentAction, false)

        verify(exactly = 1) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 1) { sPSuccessMock.invoke(any(), false) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsSuccess is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents, fromPm -> sPSuccessMock(spConsents, fromPm) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test", false)
        repeat(times) { consentManager.enqueueConsent(consentAction, false) }

        verify(exactly = times) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = times) { sPSuccessMock.invoke(any(), false) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsError is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))

        consentManager.sPConsentsSuccess = { spConsents, fromPm -> sPSuccessMock(spConsents, fromPm) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test", false)
        repeat(times) { consentManager.enqueueConsent(consentAction, false) }

        verify(exactly = times) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 0) { sPSuccessMock.invoke(any(), false) }
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
            dataStorage = dataStorage,
            executorManager = re
        )

        consentManager.sPConsentsSuccess = { spConsents, fromPm -> sPSuccessMock(spConsents, fromPm) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))

        val job = launch {
            launch { consentManager.enqueueConsent(consentAction, false) }
            launch { consentManager.enqueueConsent(consentAction, false) }
            launch { consentManager.enqueueConsent(consentAction, false) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(3)
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test", false)
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 3) { sPSuccessMock.invoke(any(), false) }
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
            executorManager = re
        )

        consentManager.sPConsentsSuccess = { spConsents, fromPm -> sPSuccessMock(spConsents, fromPm) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any(), any()) }.returns(Either.Right(consentResp))
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test", false)
        val job = launch {
            launch { consentManager.enqueueConsent(consentAction, false) }
            launch { consentManager.enqueueConsent(consentAction, false) }
            launch { consentManager.enqueueConsent(consentAction, false) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any(), any()) }
        verify(exactly = 3) { sPSuccessMock.invoke(any(), false) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }
}

package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.SPConsents
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.ExecutorManager
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
    private lateinit var service: Service

    @MockK
    private lateinit var consentManagerUtils: ConsentManagerUtils

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var successMock: (UnifiedMessageResp) -> Unit

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    private val consentResp = ConsentResp(
        uuid = "uuid_test",
        localState = "localState_test",
        legislation = Legislation.GDPR,
        userConsent = "{}",
        content = JSONObject()
    )

    private val consentAction = ConsentAction(
        requestFromPm = false,
        legislation = Legislation.GDPR,
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

        consentManager.localStateStatus = LocalStateStatus.Present("localStateTest")

        verify(exactly = 0) { service.sendConsent(any(), any()) }
        verify(exactly = 0) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and an action VERIFY that sendConsent is Not invoked`() {

        every { service.sendConsent(any(), any(), any<Env>()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        consentManager.enqueueConsent2(consentAction)

        verify(exactly = 1) { service.sendConsent(any(), any(), any<Env>()) }
        verify(exactly = 1) { consentManagerUtils.saveGdprConsent(any()) }
        verify(exactly = 1) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsSuccess is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any<Env>()) }.returns(Either.Right(consentResp))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        repeat(times) { consentManager.enqueueConsent2(consentAction) }

        verify(exactly = times) { service.sendConsent(any(), any(), any<Env>()) }
        verify(exactly = times) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }

    @Test
    fun `GIVEN a localState and random num of actions VERIFY that sPConsentsError is invoked the same times number`() {

        // random number of attempts
        val times: Int = Random.nextInt(10)

        every { service.sendConsent(any(), any(), any<Env>()) }.returns(Either.Left(RuntimeException()))

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        repeat(times) { consentManager.enqueueConsent2(consentAction) }

        verify(exactly = times) { service.sendConsent(any(), any(), any<Env>()) }
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
            executorManager = re
        )

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any<Env>()) }.returns(Either.Right(consentResp))

        val job = launch {
            launch { consentManager.enqueueConsent2(consentAction) }
            launch { consentManager.enqueueConsent2(consentAction) }
            launch { consentManager.enqueueConsent2(consentAction) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(3)
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any<Env>()) }
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
            executorManager = re
        )

        consentManager.sPConsentsSuccess = { spConsents -> sPSuccessMock(spConsents) }
        consentManager.sPConsentsError = { throwable -> sPErrorMock(throwable) }

        every { service.sendConsent(any(), any(), any<Env>()) }.returns(Either.Right(consentResp))
        consentManager.localStateStatus = LocalStateStatus.Present("localState_test")
        val job = launch {
            launch { consentManager.enqueueConsent2(consentAction) }
            launch { consentManager.enqueueConsent2(consentAction) }
            launch { consentManager.enqueueConsent2(consentAction) }
        }
        job.join()
        consentManager.enqueuedActions.assertEquals(0)
        verify(exactly = 3) { service.sendConsent(any(), any(), any<Env>()) }
        verify(exactly = 3) { sPSuccessMock.invoke(any()) }
        verify(exactly = 0) { sPErrorMock.invoke(any()) }
    }
}

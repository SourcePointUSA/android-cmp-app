package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType.* //ktlint-disable
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ClientEventManagerTest {

    @MockK
    private lateinit var consentManagerUtils: ConsentManagerUtils

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var spClient: SpClient

    private lateinit var clientEventManager: ClientEventManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        clientEventManager = ClientEventManager.create(
            logger = logger,
            consentManagerUtils = consentManagerUtils,
            spClient = spClient,
            executor = MockExecutorManager()
        )

        every { consentManagerUtils.getCcpaConsent() }.returns(Either.Right(CCPAConsentInternal()))
        every { consentManagerUtils.getGdprConsent() }.returns(Either.Right(GDPRConsentInternal()))
    }

    @Test
    fun `GIVEN 2 successfully sendConsent (GDPR, CCPA) calls, TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(2) // 2 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = ACCEPT_ALL, requestFromPm = false, campaignType = CampaignType.GDPR)) // accept the GDPR
            setAction(ConsentActionImpl(actionType = ACCEPT_ALL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            storedConsent() // first consent saved
            storedConsent() // second consent saved
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN 1 successfully sendConsent call and 1 dismiss action, TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(2) // 2 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = MSG_CANCEL, requestFromPm = false, campaignType = CampaignType.GDPR)) // accept the GDPR
            setAction(ConsentActionImpl(actionType = ACCEPT_ALL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            storedConsent() // first consent saved
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN 2 dismiss action2, TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(2) // 2 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = MSG_CANCEL, requestFromPm = false, campaignType = CampaignType.GDPR)) // accept the GDPR
            setAction(ConsentActionImpl(actionType = PM_DISMISS, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN PM flow TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(2) // 2 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = SHOW_OPTIONS, requestFromPm = false, campaignType = CampaignType.GDPR)) // accept the GDPR
            setAction(ConsentActionImpl(actionType = PM_DISMISS, requestFromPm = true, campaignType = CampaignType.GDPR)) // accept the GDPR
            setAction(ConsentActionImpl(actionType = MSG_CANCEL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            setAction(ConsentActionImpl(actionType = ACCEPT_ALL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            storedConsent()
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN 0 campaigns TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(0) // 0 campaigns GDPR and CCPA
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN 1 campaigns and 1 ACCEPT_ALL TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(1) // 1 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = ACCEPT_ALL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            storedConsent()
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }

    @Test
    fun `GIVEN 1 campaigns and 1 MSG_CANCEL TRIGGER 1 onSpFinish`() {

        every { consentManagerUtils.gdprConsentV7 }.returns(Either.Right(GDPRConsentInternal()))
        every { consentManagerUtils.ccpaConsentV7 }.returns(Either.Right(CCPAConsentInternal()))

        clientEventManager.run {
            setCampaignNumber(1) // 1 campaigns GDPR and CCPA
            setAction(ConsentActionImpl(actionType = MSG_CANCEL, requestFromPm = false, campaignType = CampaignType.CCPA)) // accept the CCPA
            checkStatus() // check the status
        }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
    }
}

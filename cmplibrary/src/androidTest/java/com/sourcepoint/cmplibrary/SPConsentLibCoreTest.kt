package com.sourcepoint.cmplibrary

import android.content.Context
import android.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManagerImpl
import com.sourcepoint.cmplibrary.exception.FailedToLoadMessages
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.ICoordinator
import com.sourcepoint.mobile_core.models.LoadMessagesException
import com.sourcepoint.mobile_core.models.SPCampaign
import com.sourcepoint.mobile_core.models.SPCampaigns
import com.sourcepoint.mobile_core.models.SPError
import com.sourcepoint.mobile_core.models.SPPropertyName
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.models.consents.SPUserData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SPConsentLibCoreTest {
    private val accountId = 22
    private val propertyId = 16893
    private val propertyName = SPPropertyName.create("mobile.multicampaign.demo")
    private val campaigns = SPCampaigns(gdpr = SPCampaign(), ccpa = SPCampaign(), usnat = SPCampaign())
    private val spClient: SpClient get() = mockk<SpClient>(relaxed = true)
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
    private fun getConsentLib(
        spClient: SpClient = this.spClient,
        coordinator: ICoordinator = Coordinator(
            accountId = accountId,
            propertyId = propertyId,
            propertyName = propertyName,
            campaigns = campaigns
        ),
        connectionManager: ConnectionManager = ConnectionManagerImpl(context),
    ): SpConsentLib = SpConsentLibMobileCore(
        propertyId = propertyId,
        language = MessageLanguage.ENGLISH,
        activity = null,
        context = context,
        coordinator = coordinator,
        connectionManager = connectionManager,
        spClient = spClient,
    )

    @Suppress("DEPRECATION")
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context).edit()

    @Test
    fun onUIReadyIsCalled() = runBlocking {
        val client = spClient
        getConsentLib(spClient = client).loadMessage()
        wr {
            verify(exactly = 1) { client.onUIReady(any()) }
            verify(exactly = 0) { client.onError(any()) }
        }
    }

    @Test
    fun whenTheresNoInternetCallsOnErrorWithNoInternetError() = runBlocking {
        val client = spClient
        getConsentLib(
            spClient = client,
            connectionManager = object : ConnectionManager { override val isConnected = false }
        ).loadMessage()
        wr {
            verify(exactly = 0) { client.onUIReady(any()) }
            verify(exactly = 1) { client.onError(ofType<NoInternetConnectionException>()) }
        }
    }

    @Test
    fun callsOnErrorIfLoadMessagesThrowsAnExceptionAndTheresNoConsentStored() = runBlocking {
        val client = spClient
        val coordinatorMock = mockk<ICoordinator>(relaxed = true)
        every { coordinatorMock.userData } returns SPUserData()
        coEvery { coordinatorMock.loadMessages(any(), any(), any()) } throws LoadMessagesException(causedBy = SPError())
        getConsentLib(spClient =  client, coordinator = coordinatorMock).loadMessage()
        wr {
            verify(exactly = 0) { client.onUIReady(any()) }
            verify(exactly = 1) { client.onError(ofType<FailedToLoadMessages>()) }
        }
    }

    @Test
    fun callsOnSPFinishedIfThereIsConsentStored() = runBlocking {
        val client = spClient
        val userData = SPUserData(gdpr = SPUserData.SPConsent(consents = GDPRConsent()))
        val coordinatorMock = mockk<ICoordinator>(relaxed = true)
        every { coordinatorMock.userData } returns userData
        coEvery { coordinatorMock.loadMessages(any(), any(), any()) } throws LoadMessagesException(causedBy = SPError())
        getConsentLib(spClient =  client, coordinator = coordinatorMock).loadMessage()
        wr {
            verify(exactly = 0) { client.onUIReady(any()) }
            verify(exactly = 0) { client.onError(any()) }
            verify(exactly = 1) { client.onSpFinished(any()) }
        }
    }

    @Test
    fun noMessageIsShownIfTheresDataFromSDKWithoutMobileCore() = runBlocking {
        val client = spClient
        // TODO: dump data from sharedprefs from previous version of the SDK
        preferences.apply {
//            putString("")
        }.commit()
        val consentLib = getConsentLib(spClient =  client)
        consentLib.loadMessage()
        wr {
//            verify(exactly = 0) { client.onUIReady(any()) }
//            verify(exactly = 0) { client.onError(any()) }
//            verify(exactly = 1) { client.onSpFinished(any()) }
        }
    }
}

package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import org.junit.After
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinLoadPmTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    private val spConfOtt = config {
        accountId = 22
        propertyName = "ott.test.suite"
        propertyId = 22231
        campaignsEnv = CampaignsEnv.PUBLIC
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private val spConfGdprNoMessage = config {
        accountId = 22
        propertyId = 29498
        propertyName = "ott-ccpa-22"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    private val spConf = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private val spConfNative = config {
        accountId = 22
        propertyId = 18958
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }
}
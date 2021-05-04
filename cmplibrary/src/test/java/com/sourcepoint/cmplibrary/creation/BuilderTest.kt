package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class BuilderTest {

    @MockK
    private lateinit var context: Activity
    @MockK
    private lateinit var spClient: SpClient

    private val gdprCampaign = SpCampaign(
        CampaignType.GDPR,
        listOf(TargetingParam("location", "EU"))
    )

    private val ccpaCamapign = SpCampaign(
        CampaignType.CCPA,
        listOf(TargetingParam("location", "EU"))
    )

    private val spConfig = SpConfig(
        22,
        "carm.uw.con",
        listOf(
            ccpaCamapign,
            gdprCampaign
        ),
        PMTab.PURPOSES,
        MessageLanguage.ENGLISH
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test(expected = RuntimeException::class)
    fun `A context object is MISSING an exception is THROWN`() {
        Builder()
            // .setContext(context)
            .setPrivacyManagerTab(PMTab.FEATURES)
            .build()
    }

    @Test(expected = RuntimeException::class)
    fun `A spClient object is MISSING an exception is THROWN`() {
        Builder()
            .setSpConfig(spConfig)
            .setContext(context)
            .build()
    }

    @Test
    fun `A privacyManagerTab is MISSING NOTHING happened`() {
        Builder()
            .setSpConfig(spConfig)
            .setSpClient(spClient)
            .setContext(context)
            .build()
    }
}

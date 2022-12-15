package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReqImpl
import org.junit.Test

class MessagesApiModelExtKtTest {

    @Test
    fun `GIVEN a CampaignReq List CREATE the correct metadata output`() {

        val campaigns = listOf(
            CampaignReqImpl(
                campaignType = CampaignType.GDPR,
                campaignsEnv = CampaignsEnv.PUBLIC,
                groupPmId = null,
                targetingParams = emptyList()
            )
        )

        campaigns.toMetadataBody().apply {
            size.assertEquals(1)
            toString().contains("ccpa").assertFalse()
            toString().contains("gdpr").assertTrue()
        }
    }
}

package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.getAppliedLegislation
import org.junit.Test

class DataModelReqExtTest {

    @Test
    fun `GIVEN a string EXTRACT the Legislation`() {
        "gdpr".getAppliedLegislation().assertEquals(CampaignType.GDPR)
        "ccpa".getAppliedLegislation().assertEquals(CampaignType.CCPA)
    }

    @Test(expected = Throwable::class)
    fun `GIVEN a not valid string THROWS an exception`() {
        "".getAppliedLegislation()
    }
}

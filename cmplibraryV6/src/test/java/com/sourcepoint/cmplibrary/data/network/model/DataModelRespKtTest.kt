package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.exception.Legislation
import org.junit.Test

class DataModelRespKtTest {

    @Test
    fun `GIVEN a string EXTRACT the Legislation`() {
        "gdpr".getAppliedLegislation().assertEquals(Legislation.GDPR)
        "ccpa".getAppliedLegislation().assertEquals(Legislation.CCPA)
    }

    @Test(expected = Throwable::class)
    fun `GIVEN a not valid string THROWS an exception`() {
        "".getAppliedLegislation()
    }
}

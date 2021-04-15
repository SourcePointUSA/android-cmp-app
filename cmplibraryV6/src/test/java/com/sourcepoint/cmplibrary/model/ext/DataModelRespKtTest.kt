package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.getAppliedLegislation
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

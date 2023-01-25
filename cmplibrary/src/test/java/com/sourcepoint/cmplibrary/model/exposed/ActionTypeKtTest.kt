package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.assertEquals
import org.junit.Test

class ActionTypeKtTest {
    @Test
    fun `Check that PmType is mapper correctly`() {
        PmType.OTT_V1.toMessageSubCategory().assertEquals(MessageSubCategory.OTT)
        PmType.OTT_V2.toMessageSubCategory().assertEquals(MessageSubCategory.NATIVE_OTT)
        PmType.APP_V1.toMessageSubCategory().assertEquals(MessageSubCategory.TCFv2)
    }
}

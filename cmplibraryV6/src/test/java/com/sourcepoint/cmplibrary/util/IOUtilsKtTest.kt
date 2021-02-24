package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertNotNull
import org.junit.Test

class IOUtilsKtTest {
    @Test
    fun `GIVEN a file`() {
        val content = "gdpr_js_receiver.js".file2String()
        content.assertNotNull()
    }
}

package com.sourcepoint.cmplibrary.util

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class IOUtilsKtTest {
    @Test
    fun GIVEN_the_receiver_VERIFY_that_the_content_is_not_null() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val receiver = context.readFromAsset("js_receiver.js")
        receiver.assertNotNull()
    }
}

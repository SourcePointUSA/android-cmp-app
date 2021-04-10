package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class BuilderTest {

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test(expected = RuntimeException::class)
    fun `A context object is MISSING an exception is THROWN`() {
        Builder()
            // .setContext(context)
            .setPrivacyManagerTab(PrivacyManagerTabK.FEATURES)
            .build()
    }

    @Test
    fun `A privacyManagerTab is MISSING NOTHING happened`() {
        Builder()
            .setContext(context)
            // .setPrivacyManagerTab(PrivacyManagerTabK.FEATURES)
            .build()
    }
}

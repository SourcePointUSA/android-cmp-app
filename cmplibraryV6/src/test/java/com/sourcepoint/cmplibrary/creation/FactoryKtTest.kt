package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.model.SpProperty
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class FactoryKtTest {

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `CREATE a new instance of ConsentLib`() {
        makeConsentLib(
            spProperty = SpProperty(22, "asfa", Env.STAGE, "asdf", "DF"),
            privacyManagerTab = PrivacyManagerTabK.FEATURES,
            context = context,
        ).assertNotNull()
    }
}

package com.sourcepoint.cmplibrary.util

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.toList
import com.sourcepoint.cmplibrary.Utils.Companion.storeTestDataObj
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class UserConsentsRegressionTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    /**
     * Test case that tests when the user has both campaigns applies value as TRUE then should return
     * sane from the userConsents() method
     */
    @Test
    fun given_stored_consent_with_save_and_exit_choices_and_applies_true_then_user_consents_should_return_true_for_both_applies() {

        val v7Consent = JSONObject("v7/stored_consent_with_save_and_exit_choices_and_applies_true.json".file2String())
        appContext.storeTestDataObj(v7Consent.toList())

        val gdprAppliesFromUserConsents = userConsents(appContext).gdpr?.consent?.applies
        gdprAppliesFromUserConsents.assertEquals(true)
        val ccpaAppliesFromUserConsents = userConsents(appContext).ccpa?.consent?.applies
        ccpaAppliesFromUserConsents.assertEquals(true)
    }

    /**
     * Test case that tests when the user has both campaigns applies value as FALSE then should return
     * sane from the userConsents() method
     */
    @Test
    fun given_stored_consent_with_save_and_exit_choices_and_applies_false_then_user_consents_should_return_false_for_both_applies() {

        val v7Consent = JSONObject("v7/stored_consent_with_save_and_exit_choices_and_applies_false.json".file2String())
        appContext.storeTestDataObj(v7Consent.toList())

        val gdprAppliesFromUserConsents = userConsents(appContext).gdpr?.consent?.applies
        gdprAppliesFromUserConsents.assertEquals(false)
        val ccpaAppliesFromUserConsents = userConsents(appContext).ccpa?.consent?.applies
        ccpaAppliesFromUserConsents.assertEquals(false)
    }
}
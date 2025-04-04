@file:Suppress("DEPRECATION")
package com.sourcepoint.cmplibrary.legacy

import android.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertFalse
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertNull
import com.example.uitestutil.assertTrue
import com.sourcepoint.mobile_core.models.consents.CCPAConsent
import kotlinx.datetime.Instant
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LegacyStateTest {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val accountId = 123
    private val propertyId = 321

    @Test
    fun testLegacyStateCanBeMappedToMobileCoreState() {
        loadLegacySharedPrefs(preferences)
        val newState = LegacyState(preferences).toState(accountId, propertyId)

        newState.apply {
            accountId.assertEquals(accountId)
            propertyId.assertEquals(propertyId)
            localState.isNotEmpty().assertTrue()
            nonKeyedLocalState.isNotEmpty().assertTrue()

            gdpr.apply {
                childPmId.assertEquals("idGDPR")
                metaData.apply {
                    sampleRate.assertEquals(1f)
                    wasSampled.assertEquals(true)
                    wasSampledAt.assertEquals(1f)
                    vendorListId.assertEquals("608badf1a22863112f750a18")
                }
                consents.apply {
                    applies.assertTrue()
                    uuid.assertEquals("gdprUUID")
                    dateCreated.assertEquals(Instant.parse("2025-03-29T13:45:32.988Z"))
                    consentStatus.apply {
                        consentedAll!!.assertTrue()
                    }
                    euconsent.assertEquals("EUCONSENT")
                    vendors.size.assertEquals(4)
                    categories.size.assertEquals(10)
                    tcData.isNotEmpty().assertTrue()
                    webConsentPayload.isNullOrEmpty().assertFalse()
                }
            }
            ccpa.apply {
                childPmId.assertEquals("idCCPA")
                metaData.apply {
                    sampleRate.assertEquals(1f)
                    wasSampled.assertEquals(true)
                }
                consents.apply {
                    applies.assertTrue()
                    uuid.assertEquals("ccpaUUID")
                    dateCreated.assertEquals(Instant.parse("2025-03-29T13:45:34.594Z"))
                    status.assertEquals(CCPAConsent.CCPAConsentStatus.ConsentedAll)
                    gppData.isNotEmpty().assertTrue()
                    webConsentPayload.isNullOrEmpty().assertFalse()
                }
            }
            usNat.apply {
                childPmId.assertEquals("idUSNAT")
                metaData.apply {
                    sampleRate.assertEquals(1f)
                    wasSampled.assertEquals(true)
                    wasSampledAt.assertEquals(1f)
                    vendorListId.assertEquals("65a01016e17a3c7a831ec515")
                }
                consents.apply {
                    applies.assertTrue()
                    uuid.assertEquals("usNatUUID")
                    dateCreated.assertEquals(Instant.parse("2025-03-29T13:47:05.359Z"))
                    gppData.isNotEmpty().assertTrue()
                    webConsentPayload.isNullOrEmpty().assertFalse()
                    consentStatus.apply {
                        consentedAll!!.assertTrue()
                    }
                    userConsents.apply {
                        vendors.size.assertEquals(0)
                        categories.size.assertEquals(13)
                        categories.all { it.consented }.assertTrue()
                    }
                }
            }
        }
    }

    @Test
    fun testMigratingLegacyStateWorks() {
        loadLegacySharedPrefs(preferences)
        migrateLegacyToNewState(preferences, accountId, propertyId).assertNotNull()
        preferences.getString(LegacyLocalState.PREFS_KEY, null).assertNull()
    }

    @Test
    fun testMigratingLegacyStateDoesntThrowEvenWhenInvalid() {
        loadLegacySharedPrefs(preferences, faultyLegacySharedPrefsXML)
        migrateLegacyToNewState(preferences, accountId, propertyId).assertNull()
        preferences.getString(LegacyLocalState.PREFS_KEY, null).assertNotNull()
    }
}

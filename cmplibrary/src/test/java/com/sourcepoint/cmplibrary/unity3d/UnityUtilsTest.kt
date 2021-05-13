package com.sourcepoint.cmplibrary.unity3d

import android.view.View
import com.sourcepoint.cmplibrary.*
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Test
import java.lang.Exception

class UnityUtilsTest {

    //region arrayToList unit-tests
    @Test
    fun `CALLING arrayToList with empty Array of TargetingParam RETURN empty List`() {
        val someArray: Array<TargetingParam> = emptyArray()
        val list = arrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
    }

    @Test
    fun `CALLING arrayToList with 1 element Array of TargetingParam RETURN 1 element List of TargetingParam`() {
        val someArray: Array<TargetingParam> = arrayOf(TargetingParam("language", "UA"))
        val list = arrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
        list[0].key.assertEquals(someArray[0].key)
        list[0].value.assertEquals(someArray[0].value)
    }

    @Test
    fun `CALLING arrayToList with empty Array of String RETURN empty Array of String`() {
        val someArray: Array<String> = emptyArray()
        val list = arrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
    }

    @Test
    fun `CALLING arrayToList with 1 element Array of String RETURN  1 element List of String`() {
        val someArray: Array<String> = arrayOf("blah")
        val list = arrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
        list[0].assertEquals(someArray[0])
    }
    //endregion

    //region throwableToException unit-tests
    @Test(expected = Exception::class)
    fun `CALLING throwableToException with Throwable RETURN Exception`() {
        val throwable: Throwable = Throwable()
        throwableToException(throwable)
    }

    @Test
    fun `CALLING throwableToException with Throwable with message RETURN Exception with same message`() {
        val myErrMessage: String = "Hello yes this is error!"
        val throwable: Throwable = Throwable(myErrMessage)
        try {
            throwableToException(throwable)
        } catch (exception: Exception) {
            exception.assertNotNull()
            exception.message.assertNotNull()
            exception.stackTrace.assertNotNull()
            exception.message!!.contains(myErrMessage).assertEquals(true)
        }
    }
    //endregion

    //region callCustomConsentGDPR unit-tests
    fun createSpConsentLib(fakeConsents: SPConsents): SpConsentLib {
        return object : SpConsentLib {
            override fun loadMessage() {}
            override fun loadMessage(authId: String) {}
            override fun loadMessage(nativeMessage: NativeMessage) {}
            override fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType) {}
            override fun showView(view: View) {}
            override fun removeView(view: View) {}
            override fun dispose() {}
            override fun customConsentGDPR(vendors: List<String>, categories: List<String>, legIntCategories: List<String>, success: (SPConsents?) -> Unit) {
                runBlocking {
                    launch {
                        // TODO: parse json stub to SPConsent object
                        // val stub = "[SPConsents(gdpr=null, ccpa=SPCCPAConsent(consent=CCPAConsent(rejectedCategories=[], rejectedVendors=[], status=consentedAll, signedLspa=false, uspstring=, thisContent={\"rejectedAll\":false,\"rejectedCategories\":[],\"rejectedVendors\":[],\"signedLspa\":false,\"status\":\"consentedAll\",\"uspstring\":\"1---\"})))]"
                        delay(1000L)
                        println(fakeConsents.toString())
                        success(fakeConsents)
                    }
                }
            }
        }
    }

    @Test
    fun `CALLING callCustomConsentGDPR RETURN SPConsents(null, null)`() {
        val vendors = arrayOf("5fbe6f050d88c7d28d765d47")
        val categories = arrayOf("60657acc9c97c400122f21f3")
        val legIntCategories = emptyArray<String>()
        val spConsentLib = createSpConsentLib(SPConsents(null, null))
        val proxy = object : UnityCustomConsentGDPRProxy {
            override fun transferCustomConsentToUnitySide(spCustomConsentsJSON: String?) {
                // In real use case UnityCustomConsentGDPRProxy is implemented c#-side
//                println("I'm in proxy -> $spCustomConsentsJSON")
                // TODO: make assert condition as delegate
                spCustomConsentsJSON.assertNotNull()
                spCustomConsentsJSON.assertEquals("{}")
            }
        }
        callCustomConsentGDPR(spConsentLib, vendors, categories, legIntCategories, proxy)
    }

    @Test
    fun `CALLING callCustomConsentGDPR RETURN SPConsents(gdpr, ccpa)`() {
        val vendors = arrayOf("5fbe6f050d88c7d28d765d47")
        val categories = arrayOf("60657acc9c97c400122f21f3")
        val legIntCategories = emptyArray<String>()
        val someValue = "someValue"
        val gdpr = SPGDPRConsent(GDPRConsent(someValue, emptyMap(), emptyMap(), JSONObject("{euconsent :$someValue}")))
        val list = listOf<String>(someValue)
        val liststr = "{rejectedCategories :$list}"
        val ccpa = SPCCPAConsent(CCPAConsent(list, emptyList(), "", true, "", JSONObject(liststr)))

        val spConsentLib = createSpConsentLib(SPConsents(gdpr, ccpa))
        val proxy = object : UnityCustomConsentGDPRProxy {
            override fun transferCustomConsentToUnitySide(spCustomConsentsJSON: String?) {
                // In real use case UnityCustomConsentGDPRProxy is implemented c#-side
                println("1 I'm in proxy -> $spCustomConsentsJSON")
                // TODO: make assert condition as delegate and move UnityCustomConsentGDPRProxy creation to fun
                spCustomConsentsJSON.assertNotNull()
                spCustomConsentsJSON?.contains(someValue)?.assertTrue()
                spCustomConsentsJSON?.contains("[\"$someValue\"]")?.assertTrue()
            }
        }
        callCustomConsentGDPR(spConsentLib, vendors, categories, legIntCategories, proxy)
    }
    //endregion
}

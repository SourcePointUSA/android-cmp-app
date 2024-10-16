package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.GCMStatus
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.*  //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.util.file2List
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN a GCM obj with ACCEPT_ALL consent RETURN all GRANTED fields`() {
        val json = "gcm/gcm_acceptAll.json".file2String()

        val output = (sut.toChoiceResp(json) as Either.Right).r
        output.gdpr!!.googleConsentMode.also {
            it!!.adStorage.assertEquals(GCMStatus.GRANTED)
            it.adPersonalization.assertEquals(GCMStatus.GRANTED)
            it.adUserData.assertEquals(GCMStatus.GRANTED)
            it.analyticsStorage.assertEquals(GCMStatus.GRANTED)
        }
    }

    @Test
    fun `GIVEN a GCM obj with REJECT_ALL consent RETURN all DENIED fields`() {
        val json = "gcm/gcm_rejectAll.json".file2String()

        val output = (sut.toChoiceResp(json) as Either.Right).r
        output.gdpr!!.googleConsentMode.also {
            it!!.adStorage.assertEquals(GCMStatus.DENIED)
            it.adPersonalization.assertEquals(GCMStatus.DENIED)
            it.adUserData.assertEquals(GCMStatus.DENIED)
            it.analyticsStorage.assertEquals(GCMStatus.DENIED)
        }
    }

    @Test
    fun `GIVEN a GCM obj with SAVE_AND_EXIT consent RETURN all DENIED fields`() {
        val json = "gcm/gcm_save_exit.json".file2String()

        val output = (sut.toChoiceResp(json) as Either.Right).r
        output.gdpr!!.googleConsentMode.also {
            it!!.adStorage.assertEquals(GCMStatus.DENIED)
            it.adPersonalization.assertEquals(GCMStatus.DENIED)
            it.adUserData.assertEquals(GCMStatus.GRANTED)
            it.analyticsStorage.assertEquals(GCMStatus.GRANTED)
        }
    }

    @Test
    fun `GIVEN a GCM obj with a missing adPersonalization field RETURN a null value for that property`() {
        val json = "gcm/gcm_one_null_key.json".file2String()

        val output = (sut.toChoiceResp(json) as Either.Right).r
        output.gdpr!!.googleConsentMode.also {
            it!!.adPersonalization.assertNull()
            it.adStorage.assertEquals(GCMStatus.GRANTED)
            it.adUserData.assertEquals(GCMStatus.GRANTED)
            it.analyticsStorage.assertEquals(GCMStatus.GRANTED)
        }
    }

    @Test
    fun `GIVEN a ACCEPT_ALL consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_accept_all.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a REJECT_ALL consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_reject_all.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a SAVE_AND_EXIT consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_save_and_exit.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a dataset RETURN a Right(ConsentAction)`() {
        val json = "action_data_1.json".file2String()
        val output = sut.toConsentAction(json)
        (output as Either.Right).r.also {
            it.actionType.name.assertEquals("SHOW_OPTIONS")
            it.choiceId.assertEquals("3108753")
            it.consentLanguage.assertEquals("EN")
            it.saveAndExitVariables.toString() assertEquals (JSONObject().toString())
            it.requestFromPm.assertEquals(false)
            it.privacyManagerId.assertNotNull()
        }
    }

    @Test
    fun `GIVEN a dataset  1RETURN a Right(ConsentAction)`() {
        val json = "action_data_2.json".file2String()
        val output = sut.toConsentAction(json)
        (output as Either.Right).r.also {
            it.actionType.name.assertEquals("REJECT_ALL")
            it.choiceId.assertNull()
            it.consentLanguage.assertEquals("EN")
            it.requestFromPm.assertEquals(true)
            it.privacyManagerId.assertNull()
        }
    }

    @Test
    fun `GIVEN a bunch of ConsentAction json files RETURN always right`() {
        val jsonList = "consent_action_examples.txt".file2List()
        jsonList.forEachIndexed { index, s ->
            print("========= TEST toConsentAction [$index]: ")
            when (val output = sut.toConsentAction(s)) {
                is Either.Left -> throw output.t
                is Either.Right -> {
                }
            }
            println("success ============")
        }
    }

    @Test
    fun `GIVEN a native layout dataset  RETURN a Right(NativeMessageModel)`() {
        val json = "native_layout.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toNativeMessageDto(json) as Either.Right).r
        nm.thisContent.assertEquals(testMap)
    }

    @Test
    fun `GIVEN a message body resp RETURN a Right(MessagesResp)`() {
        val json = "v7/messagesObj.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toMessagesResp(json) as Either.Right).r
        nm.campaigns!!.gdpr!!.run {
            val gdprTester = testMap.getMap("campaigns")!!.getMap("GDPR")!!
            type.assertEquals(CampaignType.GDPR)
            message.assertNotNull()
            dateCreated.toString().assertEquals(gdprTester["dateCreated"])
            messageMetaData!!.subCategoryId.assertEquals(MessageSubCategory.TCFv2)
            url.toString().assertEquals(gdprTester["url"].toString())
            grants!!.size.assertEquals(5)
            consentStatus!!.also {
                it.consentedAll!!.assertFalse()
                it.consentedToAny!!.assertFalse()
                it.hasConsentData!!.assertFalse()
                it.rejectedAny!!.assertTrue()
            }
            hasLocalData!!.assertFalse()
            addtlConsent.assertEquals(gdprTester["addtlConsent"].toString())
            euconsent.assertEquals(gdprTester["euconsent"].toString())
            customVendorsResponse.assertNotNull()
            childPmId.assertNull()
        }
        nm.campaigns!!.ccpa!!.run {
            val ccpaTester = testMap.getMap("campaigns")!!.getMap("CCPA")!!
            type.assertEquals(CampaignType.CCPA)
            message.assertNotNull()
            dateCreated.toString().assertEquals(ccpaTester["dateCreated"])
            messageMetaData!!.subCategoryId.assertEquals(MessageSubCategory.TCFv2)
            url.toString().assertEquals(ccpaTester["url"].toString())
            newUser!!.assertTrue()
            consentedAll!!.assertFalse()
            rejectedAll!!.assertFalse()
            signedLspa!!.assertFalse()
            rejectedCategories!!.size.assertEquals(0)
            rejectedVendors!!.size.assertEquals(0)
            uspstring.assertEquals("1YNN")
            status!!.name.assertEquals(ccpaTester["status"].toString())
        }
    }
}

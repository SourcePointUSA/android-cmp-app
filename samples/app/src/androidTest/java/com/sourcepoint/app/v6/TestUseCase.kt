package com.sourcepoint.app.v6

import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestData.ACCEPT
import com.sourcepoint.app.v6.TestData.ACCEPT_ALL
import com.sourcepoint.app.v6.TestData.CANCEL
import com.sourcepoint.app.v6.TestData.CCPA_CONSENT_LIST
import com.sourcepoint.app.v6.TestData.CONSENT_LIST_2
import com.sourcepoint.app.v6.TestData.GDPR_CONSENT_LIST_2
import com.sourcepoint.app.v6.TestData.NETWORK
import com.sourcepoint.app.v6.TestData.OPTIONS
import com.sourcepoint.app.v6.TestData.PM_CONSENT_LIST
import com.sourcepoint.app.v6.TestData.REJECT
import com.sourcepoint.app.v6.TestData.REJECT_ALL
import com.sourcepoint.app.v6.TestData.SAVE_AND_EXIT
import com.sourcepoint.app.v6.TestData.SETTINGS_OTT_EN
import com.sourcepoint.app.v6.TestData.SITE_VENDORS
import com.sourcepoint.app.v6.TestData.TITLE_GDPR
import com.sourcepoint.app.v6.TestData.VENDORS_LIST
import com.sourcepoint.app.v6.TestData.VENDORS_LIST_2
import com.sourcepoint.app.v6.TestData.ZUSTIMMEN
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.di.customCategoriesDataProd
import com.sourcepoint.app.v6.di.customVendorDataListProd
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

class TestUseCase {

    companion object {
        fun progRejectAll() {
            performClickById(R.id.reject_all_gdpr_button)
        }

        fun tapToDisableSomeConsent() {
            CONSENT_LIST_2.forEach { consent ->
                tapOnToggle2(property = consent, tapOnlyWhen = true)
            }
        }

        fun tapToEnableSomeOption() {
            CONSENT_LIST_2.forEach { consent ->
                tapOnToggle2(property = consent, tapOnlyWhen = false)
            }
        }

        fun checkSomeConsentsOff() {
            CONSENT_LIST_2.forEach { consent ->
                checkConsentState(consent, false, "tcfv2-stack")
            }
        }

        fun checkAllCcpaConsentsOn() {
            CCPA_CONSENT_LIST.forEach { consent ->
                checkConsentStateCCPA(consent, true, "ccpa-stack")
            }
        }

        fun checkCustomCategoriesData() {
            // the customCategoriesData elements are enabled
            customCategoriesDataProd.map { it.second }.forEach { consent ->
                checkConsentState(consent, true, "tcfv2-stack")
            }
            // all CONSENT_LIST_2 elements are disabled except the customCategoriesData
            CONSENT_LIST_2.subtract(customCategoriesDataProd.map { it.second }
                .toSet()).forEach { consent ->
                checkConsentState(consent, false, "tcfv2-stack")
            }
        }

        fun checkDeletedCustomCategoriesData() {
            // the customCategoriesData elements are enabled
            customCategoriesDataProd.map { it.second }.forEach { consent ->
                checkConsentState(consent, false, "tcfv2-stack")
            }
            // all CONSENT_LIST_2 elements are disabled except the customCategoriesData
            CONSENT_LIST_2.subtract(customCategoriesDataProd.map { it.second }
                .toSet()).forEach { consent ->
                checkConsentState(consent, true, "tcfv2-stack")
            }
        }

        fun checkCustomVendorDataList() {
            // the customVendorDataList elements are enabled
            customVendorDataListProd.map { it.second }.forEach { _ ->
                checkConsentStateVendor(true, "tcfv2-stack")
            }
            // all CONSENT_LIST_2 elements are disabled except the customCategoriesData
            VENDORS_LIST.subtract(customVendorDataListProd.map { it.second }
                .toSet()).forEach { _ ->
                checkConsentStateVendor(false, "tcfv2-stack")
            }
        }

        fun checkDeepLinkDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.app_dl_tv)
        }

        fun clickOnGdprReviewConsent() {
            performClickById(resId = R.id.review_consents_gdpr)
        }

        fun checkAllGdprConsentsOn() {
            GDPR_CONSENT_LIST_2.forEach { consent ->
                checkConsentState(consent, true, "tcfv2-stack")
            }
        }

        fun checkAllTogglesOFF() {
            PM_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false, "tcfv2-stack")
            }
        }

        fun checkGdprNativeTitle() {
            isDisplayedByResIdByText(resId = R.id.title_nm, text = "GDPR Lorem Ipsum")
        }

        fun tapNmDismiss() {
            performClickById(R.id.cancel)
        }

        fun tapNmAcceptAll() {
            performClickById(R.id.accept_all)
        }

        fun clickOnCustomConsent() {
            performClickById(resId = R.id.custom_consent)
        }

        fun clickOnDeleteCustomConsent() {
            performClickById(resId = R.id.delete_custom_consent)
        }

        fun clickOnCcpaReviewConsent() {
            performClickById(resId = R.id.review_consents_ccpa)
        }

        fun clickOnRefreshBtnActivity() {
            performClickById(resId = R.id.refresh_btn)
        }

        fun tapOptionWebView() {
            performClickOnWebViewByContent(OPTIONS)
        }

        fun tapCancelOnWebView() {
            performClickOnWebViewByContent(CANCEL)
        }

        fun checkWebViewDisplayedGDPRFirstLayerMessage() {
            checkTextInParagraph(TITLE_GDPR)
        }

        fun tapPartnersOnWebView() {
            performClickOnLabelWebViewByContent("Partners")
        }

        fun checkAllVendorsOff() {
            VENDORS_LIST_2.forEach { _ ->
                checkConsentStateVendor( false, "tcfv2-stack")
            }
        }

        fun tapFeaturesOnWebView() {
            performClickOnLabelWebViewByContent("Features")
        }

        fun tapPurposesOnWebView() {
            performClickOnLabelWebViewByContent("Purposes")
        }

        fun checkFeaturesTab() {
            checkTextInParagraph("Features are a use of the data that you have already agreed to share with us")
        }

        fun checkPurposesTab() {
            checkTextInParagraph("We have a need to use your data for this processing purpose that is required for us to deliver services to you.")
        }

        fun tapSiteVendorsWebView() {
            performClickPMTabSelected(SITE_VENDORS)
        }

        fun tapAcceptAllOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

        fun tapZustimmenAllOnWebView() {
            performClickOnWebViewByContent(ZUSTIMMEN)
        }

        fun tapNetworkOnWebView() {
            performClickOnLabelWebViewByContent(NETWORK)
        }

        fun tapRejectOnWebView() {
            performClickOnWebViewByContent(REJECT)
        }

        fun tapSaveAndExitWebView() {
            performClickOnWebViewByContent(SAVE_AND_EXIT)
        }

        fun tapRejectAllWebView() {
            performClickOnWebViewByContent(REJECT_ALL)
        }

        fun tapAcceptOnWebView() {
            performClickOnWebViewByContent(ACCEPT)
        }

        fun tapSettingsOnWebView() {
            performClickOnWebViewByContent(SETTINGS_OTT_EN)
        }

        fun tapAcceptCcpaOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

        fun mockModule(
            spConfig: SpConfig,
            gdprPmId: String = "",
            ccpaPmId: String = "",
            usnatPmId: String = "",
            pAuthId: String? = null,
            url: String = "",
            useGdprGroupPmIfAvailable: Boolean = false,
            pResetAll: Boolean = true,
            messageType: MessageType = MessageType.MOBILE,
            spClientObserver: List<SpClient> = emptyList(),
            diagnostic: List<Pair<String, Any?>> = emptyList(),
            connectionManager: ConnectionManager = object : ConnectionManager {
                override val isConnected = true
            },
            dismissMessageOnBackPress: Boolean = true,
        ) = module {
            single<List<SpClient?>> { spClientObserver }
            single<DataProvider> {
                object : DataProvider {
                    override val authId = pAuthId
                    override val resetAll = pResetAll
                    override val useGdprGroupPmIfAvailable: Boolean = useGdprGroupPmIfAvailable
                    override val url = url
                    override val spConfig: SpConfig = spConfig
                    override val gdprPmId: String = gdprPmId
                    override val ccpaPmId: String = ccpaPmId
                    override val messageType: MessageType = messageType
                    override val customVendorList: List<String> = customVendorDataListProd.map { it.first }
                    override val customCategories: List<String> = customCategoriesDataProd.map { it.first }
                    override val diagnostic: List<Pair<String, Any?>> = diagnostic
                }
            }
            single { connectionManager }
            single(named("dismissMessageOnBackPress")) { dismissMessageOnBackPress }
        }
    }
}

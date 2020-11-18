package com.sourcepointmeta.metaapp

class MetaAppTestsKRobot {

    private val utility by lazy { Utility() }

    suspend fun tapOnAddProperty(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickById(R.id.action_addProperty)
        }
    }

    suspend fun addPropertyWithAllFields() = apply {
        waitAndRetry {
            utility.addPropertyWith(TestData.ALL_FIELDS)
        }
    }

    suspend fun tapDismissWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByClass("message-stacksclose")
        }
    }

    suspend fun tapOnSave() = apply {
        waitAndRetry {
            performClickById(resId = R.id.action_saveProperty)
        }
    }

    suspend fun addNativeMessagePropertyDetails() = apply {
        val accountId = "22"
        val propertyId = "7094"
        val propertyName = "tcfv2.mobile.demo"
        val pmId = "179657"

        waitAndRetry {
            insertTextByResId(propId = R.id.etAccountID, text = accountId)
            insertTextByResId(propId = R.id.etPropertyId, text = propertyId)
            insertTextByResId(propId = R.id.etPropertyName, text = propertyName)
            insertTextByResId(propId = R.id.etPMId, text = pmId)
            performClickById(resId = R.id.toggleNativeMessage)
        }
    }

    suspend fun checkNativeMessageDisplayed() = apply {
        waitAndRetry {
            isDisplayedAllOf(R.id.Title)
        }
    }

    suspend fun tapShowOption() = apply {
        waitAndRetry {
            performClickById(R.id.ShowOption)
        }
    }

    suspend fun tapRejectAll() = apply {
        waitAndRetry {
            performClickById(R.id.RejectAll)
        }
    }

    suspend fun tapAcceptAll() = apply {
        waitAndRetry {
            performClickById(R.id.AcceptAll)
        }
    }

    suspend fun checkWebViewDisplayedForPrivacyManager() = apply {
        waitAndRetry {
            checkWebViewHasText(TestData.PRIVACY_MANAGER)
        }
    }

    suspend fun checkConsentListNotSelected() = apply {
        TestData.CONSENT_LIST.forEach { consent ->
            waitAndRetry {
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkWebViewDisplayedForMessage(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            checkWebViewHasText(TestData.MESSAGE)
        }
    }

    suspend fun tapRejectAllOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.PM_REJECT_ALL)
        }
    }

    suspend fun tapPMAcceptAllOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(TestData.PM_ACCEPT_ALL)
        }
    }

    suspend fun tapAcceptAllOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.ACCEPT_ALL)
        }
    }

    suspend fun checkForConsentsAreDisplayed() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.consentRecyclerView)
        }
    }

    suspend fun tapManagePreferencesOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.MANAGE_PREFERENCES)
        }
    }

    suspend fun tapSaveAndExitOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.PM_SAVE_AND_EXIT)
        }
    }

    suspend fun checkForPropertyInfoScreen() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.consentRecyclerView)
        }
    }

    suspend fun checkForPropertyInfoInList() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.swipe_layout)
        }
    }

    suspend fun checkForConsentAreDisplayed() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.consentRecyclerView)
        }
    }

    suspend fun checkForConsentAreNotDisplayed() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.tv_consentsNotAvailable)
        }
    }

    suspend fun navigateBackToListView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickContent("Navigate up")
        }
    }

    suspend fun tapOnProperty(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickById(R.id.item_view)
        }
    }

    suspend fun checkWebViewDoesNotDisplayTheMessage() = apply {
        waitAndRetry {
            checkWebViewDoesNotHasText(TestData.MESSAGE)
        }
    }

    suspend fun loadPrivacyManagerDirect(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickById(R.id.action_showPM)
        }
    }

    suspend fun swipeAndChooseResetAction() = apply {
        waitAndRetry {
            swipeAndChooseAction(R.id.reset_button, TestData.YES)
        }
    }

    suspend fun selectNativeMessageConsentList(delayExecution : Long = 0) = apply {
        TestData.NATIVE_MESSAGE_CONSENT_LIST.forEach { consent ->
            waitAndRetry(delayExecution) {
                checkConsentWebView(consent)
            }
        }
    }

    suspend fun selectPartialConsentList(delayExecution : Long = 0) = apply {
        TestData.PARTIAL_CONSENT_LIST.forEach { consent ->
            waitAndRetry(delayExecution) {
                checkConsentWebView(consent)
            }
        }
    }

    suspend fun checkInsertedProperty(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            isDisplayedByResId(R.id.action_addProperty)
        }
    }

    suspend fun checkConsentNotDisplayed() = apply {
        waitAndRetry {
            isDisplayedByResId(R.id.tv_consentsNotAvailable)
        }
    }

    suspend fun addPropertyFor(messageType : String, authentication : String) = apply {

        waitAndRetry {
            utility.addPropertyFor(messageType, authentication)
        }

    }

}
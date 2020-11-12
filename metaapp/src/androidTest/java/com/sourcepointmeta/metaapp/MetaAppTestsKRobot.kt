package com.sourcepointmeta.metaapp

class MetaAppTestsKRobot {

    suspend fun tapOnAddProperty() = apply {
        waitAndRetry {
            performClickByIdAndContent(R.id.action_addProperty, "Add Property")
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
            writeText(propId = R.id.etAccountID, text = accountId)
            writeText(propId = R.id.etPropertyId, text = propertyId)
            writeText(propId = R.id.etPropertyName, text = propertyName)
            writeText(propId = R.id.etPMId, text = pmId)
            performClickById(resId = R.id.toggleNativeMessage)
        }
    }

    suspend fun checkNativeMessageDisplayed() = apply {
        waitAndRetry {
            isDisplayAllOf(R.id.Title)
        }
    }

    suspend fun tapShowOption() = apply {
        waitAndRetry {
            performClickById(R.id.ShowOption)
        }
    }

    suspend fun isPrivacyManagerVisible() = apply {
        waitAndRetry {
            checkWebViewHasText(TestData.PRIVACY_MANAGER)
        }
    }

    suspend fun rejectAll() = apply {
        waitAndRetry {
            performClickInWebViewByContent(TestData.PM_REJECT_ALL)
        }
    }

    suspend fun isPropertyInfoScreenDisplayed() = apply {
        waitAndRetry {
            isDisplay(R.id.consentRecyclerView)
        }
    }

    suspend fun navigateBackToListView() = apply {
        waitAndRetry {
            performClickContent("Navigate up")
        }
    }

    suspend fun isAddPropertyDisplayed() = apply {
        waitAndRetry {
            isDisplay(R.id.action_addProperty)
        }
    }

    suspend fun tapOnProperty() = apply {
        waitAndRetry {
            performClickById(R.id.item_view)
        }
    }

}
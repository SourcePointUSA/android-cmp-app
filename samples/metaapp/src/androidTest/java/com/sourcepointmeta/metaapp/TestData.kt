package com.sourcepointmeta.metaapp

object TestData {

    val GDPR_CONSENT_LIST_2 = listOf(
        "Store and/or access information on a device",
        "Select basic ads",
        "Create a personalised ads profile",
//        "Select personalised ads",
//        "Measure ad performance",
//        "Measure content performance",
        "Apply market research to generate audience insights",
//        "Develop and improve products",
        "Our Custom Purpose"
    )

    val CCPA_CONSENT_LIST = listOf(
        "Category 1",
        "Category 2",
        "Category 3",
    )

    val VENDORS_LIST = listOf(
        "Unruly Group LLC",
        "QuarticON",
        "Game Accounts"
    )

    var CONSENT_LIST = arrayOf(
        "Store and/or access information on a device", "Create a personalised ads profile", "Select personalised ads",
        "Create a personalised content profile", "Select personalised content", "Measure content performance", "Apply market research to generate audience insights",
        "Develop and improve products"
    )
    var PARTIAL_CONSENT_LIST = arrayOf("Create a personalised content profile", "Select personalised content", "Measure content performance")
    var MESSAGE = "message"
    var ACCEPT = "Accept"
    var ZUSTIMMEN = "Zustimmen"
    var REJECT = "Reject"
    var OPTIONS = "Options"
    var EINSTELLUNGEN = "Einstellungen"
    var ACCEPT_ALL = "Accept All"
    var NETWORK = "Network"
    var REJECT_ALL = "Reject All"
    var SAVE_AND_EXIT = "Save & Exit"
    var PRIVACY_MANAGER = "privacy-manager"
    var SITE_VENDORS = "Site Vendors"
    var VENDOR_NAME = "google"
    var FEATURES = "Features"
    var PURPOSES = "Purposes"
    var TITLE = "GDPR Message"
    var CANCEL = "Cancel"
}

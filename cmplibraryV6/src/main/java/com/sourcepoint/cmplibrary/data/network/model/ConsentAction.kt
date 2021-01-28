package com.sourcepoint.cmplibrary.data.network.model

data class ConsentAction(
    val uuid : Int,
    val choiceId : String,
    val privacyManagerId : String,
    val pmTab : String,
    val requestFromPm: Boolean,
    val saveAndExitVariables : String,
    val consentLanguage : String,
)
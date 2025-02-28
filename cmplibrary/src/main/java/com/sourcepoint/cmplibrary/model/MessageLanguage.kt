package com.sourcepoint.cmplibrary.model

import com.sourcepoint.mobile_core.models.SPMessageLanguage

enum class MessageLanguage(val value: String) {
    BULGARIAN("BG"),
    CATALAN("CA"),
    CHINESE("ZH"),
    CROATIAN("HR"),
    CZECH("CS"),
    DANISH("DA"),
    DUTCH("NL"),
    ENGLISH("EN"),
    ESTONIAN("ET"),
    FINNISH("FI"),
    FRENCH("FR"),
    GAELIC("GD"),
    GERMAN("DE"),
    GREEK("EL"),
    HEBREW("HE"),
    HUNGARIAN("HU"),
    ICELANDIC("IS"),
    INDONESIAN("ID"),
    ITALIAN("IT"),
    JAPANESE("JA"),
    KOREAN("KO"),
    LATVIAN("LV"),
    LITHUANIAN("LT"),
    MACEDONIAN("MK"),
    MALAY("MS"),
    NORWEGIAN("NO"),
    POLISH("PL"),
    PORTUGUESE("PT"),
    ROMANIAN("RO"),
    RUSSIAN("RU"),
    SERBIAN_CYRILLIC("SR-CYRL"),
    SERBIAN_LATIN("SR-LATN"),
    SLOVAKIAN("SK"),
    SLOVENIAN("SL"),
    SPANISH("ES"),
    SWEDISH("SV"),
    TAGALOG("TL"),
    TURKISH("TR");

    fun toCore() = SPMessageLanguage.entries.find {
        it.shortCode.uppercase() == value.uppercase()
    }
}

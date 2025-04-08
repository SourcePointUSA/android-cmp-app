package com.sourcepoint.cmplibrary.model

import com.sourcepoint.mobile_core.models.SPMessageLanguage

enum class MessageLanguage(val value: String) {
    ALBANIAN("sq"),
    ARABIC("ar"),
    BASQUE("eu"),
    BOSNIAN_LATIN("bs"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE_SIMPLIFIED("zh"),
    CHINESE_TRADITIONAL("zh-hant"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    GALICIAN("gl"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    MALAY("ms"),
    MALTESE("mt"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE_BRAZIL("pt-br"),
    PORTUGUESE_PORTUGAL("pt-pt"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN_CYRILLIC("sr-cyrl"),
    SERBIAN_LATIN("sr-latn"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAGALOG("tl"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    VIETNAMESE("vi"),
    WELSH("cy");

    fun toCore() = SPMessageLanguage.entries.find {
        it.shortCode.uppercase() == value.uppercase()
    }
}

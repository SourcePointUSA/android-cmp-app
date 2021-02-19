package com.sourcepoint.gdpr_cmplibrary;

import java.util.Arrays;

public enum MessageLanguage {

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
    HUNGARIAN("HU"),
    ICELANDIC("IS"),
    ITALIAN("IT"),
    JAPANESE("JA"),
    LATVIAN("LV"),
    LITHUANIAN("LT"),
    NORWEGIAN("NO"),
    POLISH("PL"),
    PORTUGEESE("PT"),
    ROMANIAN("RO"),
    RUSSIAN("RU"),
    SERBIAN_CYRILLIC("SR-CYRL"),
    SERBIAN_LATIN("SR-LATN"),
    SLOVAKIAN("SK"),
    SLOVENIAN("SL"),
    SPANISH("ES"),
    SWEDISH("SV"),
    TURKISH("TR");

    final String language;

    MessageLanguage(String language) {
        this.language = language;
    }

    public static String[] names() {
        return Arrays.toString(MessageLanguage.values()).replaceAll("^.|.$", "").split(", ");
    }
    
    /**
     * Returns the internal String representation for this {@link MessageLanguage}. Note that this language String
     * approximates the ISO 639 language String where possible. When comparing this to your system locale, you should
     * heed the advice in {@link Locale#getLanguage()}.
     *
     * @return An ISO 639 language String, but uppercase. Never null, never empty
     */
    public String getLanguage() {
        return language;
    }

    public static MessageLanguage findByName(String name){
        for(MessageLanguage ml : values()){
            if( ml.name().equals(name)){
                return ml;
            }
        }
        return null;
    }
}

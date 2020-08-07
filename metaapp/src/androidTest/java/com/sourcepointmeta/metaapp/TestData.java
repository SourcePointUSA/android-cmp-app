package com.sourcepointmeta.metaapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestData {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String accountID = "808";
    public static String propertyID = "7376";
    public static String propertyName = "tcfv2.automation.testing";
    public static String pmID = "178786";
    public static String campaign = "public";
    public static String keyParam = "language";
    public static String valueParamFrench = "fr";
    public static String valueParamEnglish = "en";
    public static String keyParamShowOnce = "displayMode";
    public static String valueParamShowOnce = "appLaunch";
    public static String keyParamForPMAsMessage = "pm";
    public static String valueParamForPMAsMessage = "true";
    public static String authIdValue;

    public static String authID(){
        Date date = new Date();
        authIdValue = sdf.format(date);
        return authIdValue;
    }

    public static String[] CONSENT_LIST={"Select personalised content", "Information storage and access", "Personalisation", "Ad selection, delivery, reporting",
            "Content selection, delivery, reporting", "Measurement", "Measure ad performance", "Apply market research to generate audience insights",
            "Measure content performance", "Develop and improve products"};
    public static String [] PARTIAL_CONSENT_LIST={"Select personalised content", "Information storage and access"};

    public static String SHOW_MESSAGE_ALWAYS = "show message always";
    public static String SHOW_MESSAGE_ONCE = "show message once";
    public static String PM_AS_FIRST_LAYER_MESSAGE= "privacy manager as first layer message";
    public static String MESSAGE = "message";
    public static String PRIVACY_MANAGER = "privacy-manager";
    public static String NO_AUTHENTICATION = "no";
    public static String UNIQUE_AUTHENTICATION = "unique";
    public static String EXISTING_AUTHENTICATION = "same";
    public static String ACCEPT_ALL = "ACCEPT ALL";
    public static String PM_ACCEPT_ALL = "Accept All";
    public static String REJECT_ALL = "REJECT ALL";
    public static String PM_REJECT_ALL = "Reject All";
    public static String PM_SAVE_AND_EXIT = "Save & Exit";
    public static String PM_CANCEL = "Cancel";
    public static String MANAGE_PREFERENCES = "MANAGE PREFERENCES";
    public static String CONSENTS_ARE_DISPLAYED =  "consents are displayed";
    public static String CONSENTS_ARE_NOT_DISPLAYED =  "consents are not displayed";
    public static String PROPERTY_INFO_SCREEN =  "property info screen";
    public static String RESET_ACTION = "reset";
    public static String EDIT_ACTION = "edit";
    public static String DELETE_ACTION = "delete";
    public static String YES = "YES";
    public static String NO = "NO";
    public static String PARAM_VALUE = "param value";
    public static String STAGING_CAMPAIGN = "staging";
}
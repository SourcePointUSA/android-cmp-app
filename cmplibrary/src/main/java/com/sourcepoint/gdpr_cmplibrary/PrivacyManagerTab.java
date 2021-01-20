package com.sourcepoint.gdpr_cmplibrary;

import java.util.Arrays;

public enum  PrivacyManagerTab {
             DEFAULT (""),
             PURPOSES ("purposes"),
             VENDORS ("vendors"),
             FEATURES ("features");


    final String pmTab;

    PrivacyManagerTab(String privacyManagerTab){
        this.pmTab = privacyManagerTab;
    }


    public static String[] tabNames() {
        return Arrays.toString(PrivacyManagerTab.values()).replaceAll("^.|.$", "").split(", ");
    }

    public static PrivacyManagerTab findTabByName(String name){
        for(PrivacyManagerTab pmTabs : values()){
            if( pmTabs.name().equals(name)){
                return pmTabs;
            }
        }
        return null;
    }
}

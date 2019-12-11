package com.sourcepoint.test_project;

import android.app.Activity;
import android.util.Log;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.CustomVendorConsent;

import java.util.HashSet;

/*Class to retrun singl;eton instance of consentLib*/
public class ConsentLibInstance {

    private static ConsentLib consentLib = null;

    private ConsentLibInstance(Activity activity) throws ConsentLibException {
        if (consentLib == null){
            consentLib = ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",activity)
                    .setStage(true)
                    .setViewGroup(activity.findViewById(android.R.id.content))
                    .setShowPM(false)
                    .setOnMessageReady(consentLib -> Log.i("ConsentLibInstance", "onMessageReady"))
                    .setOnConsentReady(consentLib -> consentLib.getCustomVendorConsents(results -> {
                        HashSet<CustomVendorConsent> consents = (HashSet) results;
                        for(CustomVendorConsent consent : consents)
                            Log.i("ConsentLibInstance", "Consented to: "+consent);
                    }))
                    .setOnErrorOccurred(c -> Log.i("ConsentLibInstance", "Something went wrong: ", c.error))
                    .build();
        }
    }

    public static ConsentLib getConsentLibInstance(Activity activity) throws ConsentLibException{
        if (consentLib == null){
            new ConsentLibInstance(activity);
        }
        return consentLib;
    }
}

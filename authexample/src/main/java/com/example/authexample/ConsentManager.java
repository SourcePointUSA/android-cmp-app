package com.example.authexample;

import android.app.Activity;
import android.util.Log;

import com.sourcepoint.cmplibrary.Consent;
import com.sourcepoint.cmplibrary.ConsentLibBuilder;
import com.sourcepoint.cmplibrary.ConsentLibException;

import java.util.HashSet;

abstract class ConsentManager {
    private static final String TAG = "ConsentManager";

    private Activity activity;

    abstract void onConsentsReady(HashSet<Consent> consents, String consentUUID, String euconsent);

    ConsentManager(Activity activity) {
        this.activity = activity;
    }

    private ConsentLibBuilder getConsentLib(Boolean pm) {
        return ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",activity)
            .setStage(true)
            .setViewGroup(activity.findViewById(android.R.id.content))
            .setShowPM(pm)
            .setMessageTimeOut(30000)
            .setOnConsentReady(consentLib -> consentLib.getCustomVendorConsents(results -> {
                Log.d(TAG, "Interaction complete");
                HashSet<Consent> consents = (HashSet) results;
                onConsentsReady(consents, consentLib.consentUUID, consentLib.euconsent);
            }))
            .setConsentUIReady(_c -> Log.d(TAG, "Message Ready"))
            .setOnErrorOccurred(c -> Log.d(TAG, "Error Occurred: "+c.error));
    }

    void loadMessage(Boolean pm) {
        try {
            getConsentLib(pm).build().run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }

    void loadMessage(boolean pm, String authId) {
        try {
            getConsentLib(pm).setAuthId(authId).build().run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }
}


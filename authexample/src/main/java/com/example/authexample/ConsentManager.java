package com.example.authexample;

import android.app.Activity;
import android.util.Log;

import com.sourcepoint.cmplibrary.Consent;
import com.sourcepoint.cmplibrary.ConsentLib;
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
        try {
            return ConsentLib
                    .newBuilder(22, "mobile.demo", activity)
                    .setViewGroup(activity.findViewById(android.R.id.content))
                    .setTargetingParam("MyPrivacyManager", pm.toString())
                    .setMessageTimeOut(30000)
                    .setOnInteractionComplete(new ConsentLib.Callback() {
                        @Override
                        public void run(ConsentLib c) {
                            c.getCustomVendorConsents(new String[]{}, new ConsentLib.OnLoadComplete() {
                                @Override
                                public void onSuccess(Object result) {
                                    Log.d(TAG, "Interaction complete");
                                    HashSet<Consent> consents = (HashSet) result;
                                    onConsentsReady(consents, c.consentUUID, c.euconsent);
                                }
                            });
                        }
                    })
                    .setOnMessageReady(new ConsentLib.Callback() {
                        @Override
                        public void run(ConsentLib c) {
                            Log.d(TAG, "Message Ready");
                        }
                    })
                    .setOnErrorOccurred(new ConsentLib.Callback() {
                        @Override
                        public void run(ConsentLib c) {
                            Log.d(TAG, "Error Occurred: "+c.error);
                        }
                    });
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
        return null;
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


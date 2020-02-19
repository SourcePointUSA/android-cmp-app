package com.example.authexample;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sourcepoint.gdpr_cmplibrary.Consent;
import com.sourcepoint.gdpr_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;

import java.util.HashSet;

abstract class ConsentManager {
    private static final String TAG = "ConsentManager";

    private ConsentLibBuilder builder;

    abstract void onConsentsReady(HashSet<Consent> consents, String consentUUID, String euconsent);

    ConsentManager(Activity activity) {
        activity.setContentView(R.layout.activity_home);
        mainViewGroup = activity.findViewById(android.R.id.content);
        builder = getBuilder(activity);
    }

    private ViewGroup mainViewGroup;

    private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }
    private void removeView(View view) {
        if(view.getParent() != null)
            mainViewGroup.removeView(view);
    }


    private ConsentLibBuilder getBuilder(Activity a) {
        return GDPRConsentLib.newBuilder(22, "a-demo-property", 7055,"5c0e81b7d74b3c30c6852301",a)
                .setStagingCampaign(false)
                .setOnConsentUIReady(view -> {
                    showView(view);
                    Log.i(TAG, "onConsentUIReady");
                })
                .setOnConsentUIFinished(view -> {
                    removeView(view);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setOnConsentReady(consent -> {
                    Log.i(TAG, "onConsentReady");
                    Log.i(TAG, "consentString: " + (consent.consentString != null ? consent.consentString : "<empty>"));
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                })
                .setOnError(error -> {
                    Log.e(TAG, "Something went wrong: ", error);
                    Log.i(TAG, "ConsentLibErrorMessage: " + error.consentLibErrorMessage);
                });
    }

    void loadMessage() {
        builder.build().run();
    }

    void loadMessage(String authId) {
        builder.setAuthId(authId).build().run();
    }
}


package com.example.authexample;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.sourcepoint.gdpr_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

abstract class ConsentManager {
    private static final String TAG = "ConsentManager";

    private Activity activity;

    private SharedPreferences sharedPreferences ;
    abstract void onConsentsReady(GDPRUserConsent consent ,String consentUUID, String euconsent);

    abstract void showView(View view);

    abstract void removeView(View view);

    ConsentManager(Activity activity) {
        this.activity = activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    private ConsentLibBuilder buildGDPRConsentLib(PropertyConfig config) {
        return GDPRConsentLib.newBuilder(config.accountId, config.propertyName, config.propertyId, config.pmId, activity)
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
                    Log.i(TAG, "consentString: " + consent.consentString);
                    Log.i(TAG, consent.TCData.toString());
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                    String consentUUID = sharedPreferences.getString("sp.gdpr.consentUUID" , "");
                    String euConsent = sharedPreferences.getString("sp.gdpr.euconsent","");
                    this.onConsentsReady(consent , consentUUID,euConsent);
                })
                .setOnError(error -> {
                    Log.e(TAG, "Something went wrong: ", error);
                    Log.i(TAG, "ConsentLibErrorMessage: " + error.consentLibErrorMessage);
                });
    }


    void loadMessage(Boolean pm) {
        if (pm){
            buildGDPRConsentLib(getConfig(R.raw.mobile_demo_web)).build().loadPrivacyManager();
        }else {
            buildGDPRConsentLib(getConfig(R.raw.mobile_demo_web)).build().loadMessage();
        }
    }

    void loadMessage(boolean pm, String authId) {
        if(pm){
            buildGDPRConsentLib(getConfig(R.raw.mobile_demo_web)).setAuthId(authId).build().loadPrivacyManager();
        }else {
            buildGDPRConsentLib(getConfig(R.raw.mobile_demo_web)).setAuthId(authId).build().loadMessage();
        }
    }

    private PropertyConfig getConfig(int configResource){
        PropertyConfig config = null;
        try {
            config = new PropertyConfig(new JSONObject(new Scanner(activity.getResources().openRawResource(configResource)).useDelimiter("\\A").next()));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse config file.", e);
        }
        return config;
    }
}


package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.GDPRConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.UserConsent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private GDPRConsentLib gdprConsentLib;

    private GDPRConsentLib buildGDPRConsentLib() throws ConsentLibException {
        return GDPRConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setViewGroup(findViewById(android.R.id.content))
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    if(consent.status == UserConsent.ConsentStatus.acceptedNone){
                        Log.i(TAG, "There are no accepted vendors/purposes.");
                    } else if(consent.status == UserConsent.ConsentStatus.acceptedAll){
                        Log.i(TAG, "All vendors/purposes were accepted.");
                    } else {
                        for (String vendorId : consent.acceptedVendors) {
                            Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                        }
                        for (String purposeId : consent.acceptedCategories) {
                            Log.i(TAG, "The category " + purposeId + " was accepted.");
                        }
                    }
                })
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            gdprConsentLib = buildGDPRConsentLib();
            gdprConsentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                gdprConsentLib = buildGDPRConsentLib();
                gdprConsentLib.showPm();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gdprConsentLib != null ) { gdprConsentLib.destroy(); }
    }
}
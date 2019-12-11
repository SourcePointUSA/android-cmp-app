package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.CustomVendorConsent;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ConsentLib consentLib = null;

    private ConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        /*return ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> consentLib.getCustomVendorConsents(results -> {
                    HashSet<CustomVendorConsent> consents = (HashSet) results;
                    for(CustomVendorConsent consent : consents)
                        Log.i(TAG, "Consented to: "+consent);
                }))
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();*/
        return ConsentLibInstance.getConsentLibInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (consentLib == null){
                consentLib = buildAndRunConsentLib(false);
            }
            consentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*check if consentLib object is already present in bundle object if yes then reuse it*/
        if (savedInstanceState != null && savedInstanceState.getSerializable("consentLib") != null){
            consentLib = (ConsentLib) savedInstanceState.getSerializable("consentLib");
        }
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                if (consentLib == null){
                    consentLib = buildAndRunConsentLib(false);
                }
                consentLib.run();

            } catch (ConsentLibException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*store consentLib object in bundle*/
        outState.putSerializable("consentLib",consentLib);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*check if consentLib object is already present in bundle object if yes then reuse it*/
        if (savedInstanceState.getSerializable("consentLib") != null)
        consentLib =(ConsentLib)savedInstanceState.getSerializable("consentLib");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(consentLib != null ) { consentLib.destroy(); }
    }
}
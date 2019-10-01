package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.CustomVendorConsent;
import com.sourcepoint.cmplibrary.CustomPurposeConsent;
import com.sourcepoint.cmplibrary.ConsentLibException;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ConsentLib consentLib;

    private ConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return ConsentLib.newBuilder(808, "sourcepointnewscript.com", 4601,"5cacf9b2557d160781a25c6a",this)
       // return ConsentLib.newBuilder(22, "suryakant.com", 2372,"5cacf9b2557d160781a25c6a",this)
                .setViewGroup(findViewById(android.R.id.content))
                // optional, set custom targeting parameters value can be String and Integer
                .setTargetingParam("MyPrivacyManager", showPM.toString())
                //optional,  set message time out , default is 5 seconds
                .setMessageTimeOut(30000)
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "The message is about to be shown."))
                .setOnConsentReady(c -> {
                    Log.d("msgInteraction", "called");
                    try {
                        c.getCustomVendorConsents(new String[]{}, result -> {
                            HashSet<CustomVendorConsent> consents = (HashSet) result;
                            String myImportantVendorId = "5bf7f5c5461e09743fe190b3";
                            for (CustomVendorConsent consent : consents)
                                if (consent.id.equals(myImportantVendorId))
                                    Log.i(TAG, "Consented to My Important Vendor: " + consent.name);
                        });

                        c.getCustomPurposeConsents(result -> {
                            HashSet<CustomPurposeConsent> consents = (HashSet) result;
                            for (CustomPurposeConsent consent : consents)
                                Log.i(TAG, "Consented to purpose: " + consent.name);
                        });

                        // Example usage of getting IAB vendor consent results for a list of vendors
                        boolean[] IABVendorConsents = c.getIABVendorConsents(new int[]{81, 82});
                        Log.i(TAG, String.format("Consented to IAB vendors: 81 -> %b, 82 -> %b",
                                IABVendorConsents[0],
                                IABVendorConsents[1]
                        ));

                        // Example usage of getting IAB purpose consent results for a list of purposes
                        boolean[] IABPurposeConsents = c.getIABPurposeConsents(new int[]{2, 3});
                        Log.i(TAG, String.format("Consented to IAB purposes: 2 -> %b, 3 -> %b",
                                IABPurposeConsents[0],
                                IABPurposeConsents[1]
                        ));

                    } catch (ConsentLibException e) {
                        e.printStackTrace();
                    }
                })
                .setOnErrorOccurred(c -> Log.d(TAG, "Something went wrong: ", c.error))
                // generate ConsentLib at this point modifying builder will not do anything
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            consentLib = buildAndRunConsentLib(false);
            consentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                consentLib = buildAndRunConsentLib(true);
                consentLib.run();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(consentLib != null ) { consentLib.destroy(); }
    }
}
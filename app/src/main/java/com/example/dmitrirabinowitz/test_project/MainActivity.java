package com.example.dmitrirabinowitz.test_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.cmplibrary.ConsentLib;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // use step pattern for building ConsentLib to enforce proper parameters
        ConsentLib cLib = ConsentLib.newBuilder()
                // required, must be set first used to render WebView and save consent data
                .setActivity(this)
                // required, must be set second used to find account
                .setAccountId(22)
                // required, must be set third used to find scenario
                .setSiteName("app.ios.cmp")
                // optional, used for logging purposes for which page of the app the consent lib was
                // rendered on
                .setPage("main")
                // optional, used for running stage campaigns
                .setStage(false)
                // optional, used for running against our stage endpoints
                .setInternalStage(true)
                // optional, should not ever be needed provide a custom url for the messaging page
                // (overrides internal stage)
                .setInAppMessagePageUrl(null)
                // optional, should not ever be needed provide a custom domain for mms (overrides
                // internal stage)
                .setMmsDomain(null)
                // optional, should not ever be needed provide a custom domain for cmp (overrides
                // internal stage)
                .setCmpDomain(null)
                // optional, if not provided will render WebView on
                // Activity.getWindow().getDecorView().findViewById(android.R.id.content)
                .setViewGroup(null)
                // optional, set custom targeting parameters supports Strings and Integers
                .setTargetingParam("a", "c")
                .setTargetingParam("c", 100)
                // optional, sets debug level defaults to OFF
                .setDebugLevel(ConsentLib.DebugLevel.DEBUG)
                // optional, callback triggered when message data is loaded when called message data
                // will be available as String at cLib.msgJSON
                .setOnReceiveMessageData(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "msgJSON from backend: " + c.msgJSON);
                    }
                })
                // optional, callback triggered when message choice is selected when called choice
                // type will be available as Integer at cLib.choiceType
                .setOnMessageChoiceSelect(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "Choice type selected by user: " + c.choiceType.toString());
                    }
                })
                // optional, callback triggered when consent data is captured when called
                // euconsent will be available as String at cLib.euconsent and under
                // PreferenceManager.getDefaultSharedPreferences(activity).getString(EU_CONSENT_KEY, null);
                // consentUUID will be available as String at cLib.consentUUID and under
                // PreferenceManager.getDefaultSharedPreferences(activity).getString(CONSENT_UUID_KEY null);
                .setOnInteractionComplete(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "euconsent prop: " + c.euconsent);
                        Log.i(TAG, "consentUUID prop: " + c.consentUUID);
                        Log.i(TAG, "euconsent in shared preferences: " + sharedPref.getString(ConsentLib.EU_CONSENT_KEY, null));
                        Log.i(TAG, "consentUUID in shared preferences: " + sharedPref.getString(ConsentLib.CONSENT_UUID_KEY, null));
                        Log.i(TAG, "IABConsent_SubjectToGDPR in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_SUBJECT_TO_GDPR, null));
                        Log.i(TAG, "IABConsent_ConsentString in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_CONSENT_STRING, null));
                        Log.i(TAG, "IABConsent_ParsedPurposeConsents in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_PARSED_PURPOSE_CONSENTS, null));
                        Log.i(TAG, "IABConsent_ParsedVendorConsents in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_PARSED_VENDOR_CONSENTS, null));

                        c.getCustomVendorConsents(
                                new String[]{"5bc76807196d3c5730cbab05", "5bc768d8196d3c5730cbab06"},
                                new ConsentLib.OnLoadComplete() {
                                    public void onLoadCompleted(Object result) {
                                        Log.i(TAG, "custom vendor consent 1: " + ((boolean[]) result)[0]);
                                        Log.i(TAG, "custom vendor consent 2: " + ((boolean[]) result)[1]);
                                    }
                                });

                        c.getPurposeConsents(
                                new ConsentLib.OnLoadComplete() {
                                    public void onLoadCompleted(Object result) {
                                        ConsentLib.PurposeConsent[] results = (ConsentLib.PurposeConsent[])result;
                                        for (ConsentLib.PurposeConsent purpose : results) {
                                            Log.i(TAG, "Consented to purpose: " + purpose.name);
                                        }
                                    }
                                });
                        c.getPurposeConsent(
                                "5bc4ac5c6fdabb0010940ab1",
                                new ConsentLib.OnLoadComplete() {
                                    public void onLoadCompleted(Object result) {
                                        Log.i(TAG, "Consented to Measurement purpose: " + ((Boolean) result).toString());
                                    }
                                });
                    }
                })
                // generate ConsentLib at this point modifying builder will not do anything
                .build();

        // begins rendering of WebView in background until message is displayed at which point
        // WebView will take over view of page
        cLib.run();

        // Should set immediately
        Log.i(TAG, "IABConsent_CMPPresent in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_CMP_PRESENT, null));
    }
}

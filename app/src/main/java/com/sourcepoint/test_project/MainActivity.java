package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;


import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewGroup mainViewGroup;

    private void showMessageWebView(WebView webView) {
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.bringToFront();
        webView.requestLayout();
        mainViewGroup.addView(webView);
    }
    private void removeWebView(WebView webView) {
        if(webView.getParent() != null)
            mainViewGroup.removeView(webView);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStagingCampaign(true)
                .setOnConsentUIReady(consentLib -> {
                    showMessageWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIReady");
                })
                .setOnConsentUIFinished(consentLib -> {
                    removeWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setTargetingParam("aqui_eh", "favela")
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    GDPRUserConsent consent = consentLib.userConsent;
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                })
                .setOnError(consentLib -> {
                    Log.e(TAG, "Something went wrong: ", consentLib.error);
                    Log.i(TAG, "ConsentLibErrorMessage: " + consentLib.error.consentLibErrorMessage);
                    removeWebView(consentLib.webView);
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            buildGDPRConsentLib().showPm();
        });
    }
}
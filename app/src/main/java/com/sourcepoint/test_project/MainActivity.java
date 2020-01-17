package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.sourcepoint.cmplibrary.GDPRConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.UserConsent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private GDPRConsentLib gdprConsentLib;
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

    private GDPRConsentLib buildGDPRConsentLib() throws ConsentLibException {
        return GDPRConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStagingCampaign(true)
                .setOnConsentUIReady(consentLib -> {
                    showMessageWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIReady");
                })
                //.setViewGroup(findViewById(android.R.id.content))
                .setOnConsentUIFinished(consentLib -> {
                    removeWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                })
                .setOnError(consentLib -> {
                    Log.i(TAG, "Something went wrong: ", consentLib.error);
                    removeWebView(consentLib.webView);
                })
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
        mainViewGroup = findViewById(android.R.id.content);
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
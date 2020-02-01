package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.NativeMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    private ViewGroup mainViewGroup;

    private void showMessage(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);

        }

    }
    private void removeWebView(View view) {
        if(view.getParent() != null)
            mainViewGroup.removeView(view);
    }

    public void setCallBacks(GDPRConsentLib consentLib) {
        this.findViewById(R.id.AcceptAll).setOnClickListener(_v -> {
            consentLib.onMsgAccepted();
        });

        this.findViewById(R.id.RejectAll).setOnClickListener(_v -> {
            consentLib.onMsgRejected();
        });

        this.findViewById(R.id.ShowOptions).setOnClickListener(_v -> {
            consentLib.onMsgShowOptions();
        });

        this.findViewById(R.id.Cancel).setOnClickListener(_v -> {
            consentLib.onMsgCancel();
        });
    }

    private void setNativeMessageView(GDPRConsentLib c){
        setCallBacks(c);
    }



    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStagingCampaign(false)
                .setOnConsentUIReady(view -> {
                    showMessage(view);
                    Log.i(TAG, "onConsentUIReady");
                })
                .setOnConsentUIFinished(view -> {
                    removeWebView(view);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setOnConsentReady(consent -> {
                    Log.i(TAG, "onConsentReady");
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
                })
                .build();
    }

    private CCPAConsentLib buildCCPAConsentLib() {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
//                .setStagingCampaign(true)
                //.setTargetingParam("params", "true")
                .setOnConsentUIReady(consentLib -> {

                    Log.i(TAG, "onConsentUIReady");
                })
                .setOnConsentUIFinished(consentLib -> {
                    removeWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone){
                        Log.i(TAG, "There are no rejected vendors/purposes.");
                    } else if(consent.status == UserConsent.ConsentStatus.rejectedAll){
                        Log.i(TAG, "All vendors/purposes were rejected.");
                    } else {
                        for (String vendorId : consent.rejectedVendors) {
                            Log.i(TAG, "The vendor " + vendorId + " was rejected.");
                        }
                        for (String purposeId : consent.rejectedCategories) {
                            Log.i(TAG, "The category " + purposeId + " was rejected.");
                        }
                    }
                })
                .setOnError(consentLib -> {
                    Log.e(TAG, "Something went wrong: ", consentLib.error);
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "calling runNative() ... ");
        //buildCCPAConsentLib().run();
        buildGDPRConsentLib().run(new NativeMessage(this));
        //buildGDPRConsentLib().runNative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            //buildCCPAConsentLib().showPm();
            buildGDPRConsentLib().showPm();
        });
    }
}
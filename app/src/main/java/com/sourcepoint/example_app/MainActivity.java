package com.sourcepoint.example_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.NativeMessage;
import com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    private ViewGroup mainViewGroup;

    private PropertyConfig config;

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

    private PropertyConfig getConfig(int configResource){
        PropertyConfig config = null;
        try {
            config = new PropertyConfig(new JSONObject(new Scanner(getResources().openRawResource(configResource)).useDelimiter("\\A").next()));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse config file.", e);
        }
        return config;
    }

    private GDPRConsentLib buildGDPRConsentLib() {

        SharedPreferences sharedPref = this.getSharedPreferences("myshared", Context.MODE_PRIVATE);
        if(!sharedPref.contains("MyAppsAuthId")){
            String uniqueID = UUID.randomUUID().toString();
            System.out.println("cookie ["+ uniqueID +"]");
            sharedPref.edit().putString("MyAppsAuthId", uniqueID).apply();
        }

        return GDPRConsentLib.newBuilder(config.accountId, config.propertyName, config.propertyId, config.pmId,this)
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
                    Log.i(TAG, "uuid: " + consent.uuid );
                    Log.i(TAG, "consentString: " + consent.consentString);
                    Log.i(TAG, "TCData: " + consent.TCData);
                    Log.i(TAG, "vendorGrants: " + consent.vendorGrants);
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                    for (String purposeId : consent.legIntCategories) {
                        Log.i(TAG, "The legIntCategory " + purposeId + " was accepted.");
                    }
                    for (String specialFeatureId : consent.specialFeatures) {
                        Log.i(TAG, "The specialFeature " + specialFeatureId + " was accepted.");
                    }
                })
                .setOnError(error -> {
                    Log.e(TAG, "Something went wrong: ", error);
                    Log.e(TAG, "ConsentLibErrorMessage: " + error.consentLibErrorMessage);
                })
                .setOnPMReady(()-> Log.i(TAG, "PM Ready"))
                .setOnMessageReady(()-> Log.i(TAG, "Message Ready"))
                .setOnPMFinished(()-> Log.i(TAG, "PM Finished"))
                .setOnMessageFinished(()-> Log.i(TAG, "Message Finished"))
                .setOnAction(actionType  -> Log.i(TAG , "ActionType : "+actionType.toString()))
                .setAuthId(sharedPref.getString("MyAppsAuthId",""))
                .build();
    }

    private NativeMessage buildNativeMessage(){
        return new NativeMessage(this){
            @Override
            public void init(){
                super.init();
                // When using a customized layout one can completely override the init method
                // not calling super.init() and inflating the native view with the chosen layout instead.
                // In this case its important to set all the default child views using the setter methods
                // like its done in the super.init()
            }
            @Override
            public void setAttributes(NativeMessageAttrs attrs){
                super.setAttributes(attrs);
                //Here one can extend this method in order to set customized attributes other then the ones
                //already set in the super.setAttributes. No need to completely override this method.
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "init");
        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        config = getConfig(R.raw.mobile_demo_web);
        findViewById(R.id.review_consents).setOnClickListener(_v -> buildGDPRConsentLib().showPm());
        findViewById(R.id.open_activity).setOnClickListener(_v -> startActivity(new Intent(this, MainActivityAuthId.class)));
    }
}
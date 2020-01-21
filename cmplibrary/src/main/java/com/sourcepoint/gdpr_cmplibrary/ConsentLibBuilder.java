package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ConsentLibBuilder {
    private final JSONObject targetingParams = new JSONObject();

    private final String TAG = this.getClass().getName();

    Activity activity;
    int accountId, propertyId ;
    String property;
    String mmsDomain, cmpDomain, msgDomain;
    String page = "";
    ViewGroup viewGroup = null;
    GDPRConsentLib.Callback onAction, onConsentReady, onError, onConsentUIReady, onConsentUIFinished;
    boolean staging, stagingCampaign, shouldCleanConsentOnError;

    SourcePointClient sourcePointClient;

    String targetingParamsString = null;
    String authId = null;
    String pmId = "";
    GDPRConsentLib.DebugLevel debugLevel = GDPRConsentLib.DebugLevel.OFF;
    long defaultMessageTimeOut = 10000;

    StoreClient storeClient;


    ConsentLibBuilder(Integer accountId, String property, Integer propertyId , String pmId , Activity activity) {
        this.accountId = accountId;
        this.propertyId =propertyId;
        this.property = property;
        this.pmId = pmId;
        this.activity = activity;
        mmsDomain = cmpDomain = msgDomain = null;
        staging = stagingCampaign = false;
        shouldCleanConsentOnError = true;
        GDPRConsentLib.Callback noOpCallback = new GDPRConsentLib.Callback() {
            @Override
            public void run(GDPRConsentLib c) {
            }
        };
        onAction = onConsentReady = onError = onConsentUIReady = onConsentUIFinished = noOpCallback;
        storeClient = new StoreClient(PreferenceManager.getDefaultSharedPreferences(activity));
    }

    // TODO: add what are the possible choices returned to the Callback
    /**
     *  <b>Optional</b> Sets the Callback to be called when the user selects an option on the WebView.
     *  The selected choice will be available in the instance variable GDPRConsentLib.choiceType
     * @param c - a callback that will be called when the user selects an option on the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnMessageChoiceSelect(GDPRConsentLib.Callback c) {
        onAction = c;
        return this;
    }

    /**
     *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
     *  either by closing it, canceling or accepting the terms.
     * @param c - Callback to be called when the user finishes interacting with the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentReady(GDPRConsentLib.Callback c) {
        onConsentReady = c;
        return this;
    }

    /**
     * Called when the Dialog message is about to be shown
     * @param callback to be called when the message is ready to be displayed
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentUIReady(GDPRConsentLib.Callback callback) {
        onConsentUIReady = callback;
        return this;
    }

    /**
     * Called when the Dialog message is about to disapear
     * @param callback to be called when the message is ready to disapear
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentUIFinished(GDPRConsentLib.Callback callback) {
        onConsentUIFinished = callback;
        return this;
    }

    /**
     *  <b>Optional</b> Sets a Callback to be called when something goes wrong in the WebView
     * @param callback called when something wrong happens in the webview
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnError(GDPRConsentLib.Callback callback) {
        onError = callback;
        return this;
    }

    /**
     * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
     * campaigns. <b>Default:</b> false
     * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setStagingCampaign(boolean st) {
        stagingCampaign = st;
        return this;
    }

    public ConsentLibBuilder setShouldCleanConsentOnError(Boolean shouldCleanConsentOnError) {
        this.shouldCleanConsentOnError = shouldCleanConsentOnError;
        return this;
    }

    /**
     * <b>Optional</b> This parameter refers to SourcePoint's environment itself. True for staging
     * or false for production. <b>Default:</b> false
     * @param st - True for staging or false for production
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setInternalStage(boolean st) {
        staging = st;
        return this;
    }

    public ConsentLibBuilder setAuthId(String authId) {
        this.authId = authId;
        return this;
    }

    public ConsentLibBuilder setTargetingParam(String key, Integer val)  {
        return setTargetingParam(key, (Object) val);
    }

    public ConsentLibBuilder setTargetingParam(String key, String val) {
        return setTargetingParam(key, (Object) val);
    }

    private ConsentLibBuilder setTargetingParam(String key, Object val) {
        try {
            this.targetingParams.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, "Error trying to parse targetting param: [" + key + ", " + val + "]", e);
        }
        return this;
    }



    /**
     * <b>Optional</b> Sets the DEBUG level.
     * <i>(Not implemented yet)</i>
     * <b>Default</b>{@link GDPRConsentLib.DebugLevel#DEBUG}
     * @param l - one of the values of {@link GDPRConsentLib.DebugLevel#DEBUG}
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setDebugLevel(GDPRConsentLib.DebugLevel l) {
        debugLevel = l;
        return this;
    }

    private void setTargetingParamsString() {
        targetingParamsString = targetingParams.toString();
    }

    private void setSourcePointClient(){
        sourcePointClient = new SourcePointClient(accountId, property + "/" + page, propertyId, stagingCampaign, staging, targetingParamsString, authId);
    }

    /**
     * Run internal tasks and build the GDPRConsentLib. This method will validate the
     * data coming from the previous Builders and throw {@link ConsentLibException.BuildException}
     * in case something goes wrong.
     * @return GDPRConsentLib | ConsentLibNoOp
     * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
     */
    public GDPRConsentLib build() {

        setTargetingParamsString();
        setSourcePointClient();
        return new GDPRConsentLib(this);
    }

    public ConsentLibBuilder setMessageTimeOut(long milliSecond){
        this.defaultMessageTimeOut = milliSecond;
        return this;
    }
}
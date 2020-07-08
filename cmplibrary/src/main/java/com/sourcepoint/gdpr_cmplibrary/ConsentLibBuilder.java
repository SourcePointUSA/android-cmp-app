package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

@SuppressWarnings("unused")
public class ConsentLibBuilder {
    private final JSONObject targetingParams = new JSONObject();

    private final String TAG = this.getClass().getName();

    public  static final long DEFAULT_MESSAGE_TIMEOUT = 10000;

    protected GDPRConsentLib.OnConsentUIReadyCallback onConsentUIReady;
    protected GDPRConsentLib.OnConsentUIFinishedCallback onConsentUIFinished;
    protected GDPRConsentLib.OnConsentReadyCallback onConsentReady;
    protected GDPRConsentLib.OnErrorCallback onError;
    protected GDPRConsentLib.pmReadyCallback pmReady;
    protected GDPRConsentLib.messageReadyCallback messageReady;
    protected GDPRConsentLib.pmFinishedCallback pmFinished;
    protected GDPRConsentLib.messageFinishedCallback messageFinished;
    protected GDPRConsentLib.onActionCallback onAction;
    boolean staging, stagingCampaign, shouldCleanConsentOnError;

    SourcePointClient sourcePointClient;

    String targetingParamsString = null;
    String authId = null;
    GDPRConsentLib.DebugLevel debugLevel = GDPRConsentLib.DebugLevel.OFF;
    long messageTimeOut;

    StoreClient storeClient;
    private CountDownTimer timer;

    PropertyConfig propertyConfig;
    private Context context;


    ConsentLibBuilder(Integer accountId, String property, Integer propertyId , String pmId , Context context) {
        init(accountId, property, propertyId , pmId , context);
    }

    private void init(Integer accountId, String propertyName, Integer propertyId , String pmId , Context context){
        //TODO: add a constructor method that takes PropertyConfig class as parameter
        propertyConfig = new PropertyConfig(accountId, propertyId, propertyName, pmId);
        staging = stagingCampaign = false;
        shouldCleanConsentOnError = true;
        messageTimeOut = DEFAULT_MESSAGE_TIMEOUT;
        this.context = context;
    }

    protected StoreClient getStoreClient(){
        return new StoreClient(PreferenceManager.getDefaultSharedPreferences(context));
    }

    protected ConnectivityManager getConnectivityManager(){
        return  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    UIThreadHandler getUIThreadHandler(){
        return new UIThreadHandler(context.getMainLooper());
    }

    Context getContext(){
        return context;
    }

    /**
     *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
     *  either by closing it, canceling or accepting the terms.
     * @param callback - Callback to be called when the user finishes interacting with the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentReady( GDPRConsentLib.OnConsentReadyCallback callback) {
        onConsentReady = callback;
        return this;
    }

    /**
     * Called when the Dialog message is about to be shown
     * @param callback to be called when the message is ready to be displayed
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentUIReady(GDPRConsentLib.OnConsentUIReadyCallback callback) {
        onConsentUIReady = callback;
        return this;
    }

    public ConsentLibBuilder setOnPMReady(GDPRConsentLib.pmReadyCallback callback) {
        this.pmReady = callback;
        return this;
    }

    public ConsentLibBuilder setOnMessageReady(GDPRConsentLib.messageReadyCallback callback) {
        this.messageReady = callback;
        return this;
    }

    /**
     * Called when the Dialog message is about to disapear
     * @param callback to be called when the message is ready to disapear
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentUIFinished(GDPRConsentLib.OnConsentUIFinishedCallback callback) {
        onConsentUIFinished = callback;
        return this;
    }

    public ConsentLibBuilder setOnPMFinished(GDPRConsentLib.pmFinishedCallback callback) {
        this.pmFinished = callback;
        return this;
    }

    public ConsentLibBuilder setOnMessageFinished(GDPRConsentLib.messageFinishedCallback callback) {
        this.messageFinished = callback;
        return this;
    }

    public ConsentLibBuilder setOnAction(GDPRConsentLib.onActionCallback callback){
        this.onAction = callback;
        return this;
    }

    /**
     *  <b>Optional</b> Sets a Callback to be called when something goes wrong in the WebView
     * @param callback called when something wrong happens in the webview
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnError(GDPRConsentLib.OnErrorCallback callback) {
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

    String getTargetingParamsString() {
        return targetingParams.toString();
    }

    protected SourcePointClient getSourcePointClient(){
        return new SourcePointClient(new OkHttpClient(), spClientConfig() , getConnectivityManager());
    }

    private SourcePointClientConfig spClientConfig(){
        return new SourcePointClientConfig(
                propertyConfig,
                stagingCampaign,
                staging,
                getTargetingParamsString(),
                authId
        );
    }

    /**
     * Run internal tasks and build the GDPRConsentLib. This method will validate the
     * data coming from the previous Builders and throw {@link ConsentLibException.BuildException}
     * in case something goes wrong.
     * @return GDPRConsentLib | ConsentLibNoOp
     * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
     */
    public GDPRConsentLib build() {
        return getConsentLib();
    }

    CountDownTimer getTimer(Runnable onFinish) {
        return new CountDownTimer(messageTimeOut, messageTimeOut) {
            @Override
            public void onTick(long millisUntilFinished) {     }
            @Override
            public void onFinish() {
                onFinish.run();
                cancel();
            }
        };
    }

    public ConsentLibBuilder setMessageTimeOut(long milliSecond){
        this.messageTimeOut = milliSecond;
        return this;
    }

    protected GDPRConsentLib getConsentLib(){
        return new GDPRConsentLib(this);
    }
}
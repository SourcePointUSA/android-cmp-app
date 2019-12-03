package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.os.Build;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ConsentLibBuilder {
    private final JSONObject targetingParams = new JSONObject();

    Activity activity;
    int accountId, siteId ;
    String siteName;
    String mmsDomain, cmpDomain, msgDomain;
    String page = "";
    ViewGroup viewGroup = null;
    ConsentLib.Callback onMessageChoiceSelect, onConsentReady, onErrorOccurred, onMessageReady;
    boolean staging, stagingCampaign, newPM , isShowPM, shouldCleanConsentOnError;
    EncodedParam targetingParamsString = null;
    EncodedParam authId = null;
    String pmId = "";
    ConsentLib.DebugLevel debugLevel = ConsentLib.DebugLevel.OFF;
    long defaultMessageTimeOut = 10000;

    ConsentLibBuilder(Integer accountId, String siteName,Integer siteId ,String pmId ,Activity activity) {
        this.accountId = accountId;
        this.siteId =siteId;
        this.siteName = siteName;
        this.pmId = pmId;
        this.activity = activity;
        mmsDomain = cmpDomain = msgDomain = null;
        staging = stagingCampaign = newPM = isShowPM =false;
        ConsentLib.Callback noOpCallback = new ConsentLib.Callback() {
            @Override
            public void run(ConsentLib c) {
            }
        };
        onMessageChoiceSelect = onConsentReady = onErrorOccurred = onMessageReady = noOpCallback;
    }

    /**
     *  <b>Optional</b> Sets the page name in which the WebView was shown. Used for logging only.
     * @param p - a string representing page, e.g "/home"
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setPage(String p) {
        page = p;
        return this;
    }

    /**
     *  <b>Optional</b> Sets the view group in which WebView will will be rendered into.
     *  If it's not called or called with null, the MainView will be used instead.
     *  In case the main view is not a ViewGroup, a BuildException will be thrown during
     *  when build() is called.
     * @param v - the view group
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setViewGroup(ViewGroup v) {
        viewGroup = v;
        return this;
    }

    // TODO: add what are the possible choices returned to the Callback
    /**
     *  <b>Optional</b> Sets the Callback to be called when the user selects an option on the WebView.
     *  The selected choice will be available in the instance variable ConsentLib.choiceType
     * @param c - a callback that will be called when the user selects an option on the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnMessageChoiceSelect(ConsentLib.Callback c) {
        onMessageChoiceSelect = c;
        return this;
    }

    /**
     *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
     *  either by closing it, canceling or accepting the terms.
     *  At this point, the following keys will available populated in the sharedStorage:
     *  <ul>
     *      <li>{@link ConsentLib#EU_CONSENT_KEY}</li>
     *      <li>{@link ConsentLib#CONSENT_UUID_KEY}</li>
     *      <li>{@link ConsentLib#IAB_CONSENT_SUBJECT_TO_GDPR}</li>
     *      <li>{@link ConsentLib#IAB_CONSENT_CONSENT_STRING}</li>
     *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_PURPOSE_CONSENTS}</li>
     *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_VENDOR_CONSENTS}</li>
     *  </ul>
     *  Also at this point, the methods {@link ConsentLib#getCustomVendorConsents},
     *  {@link ConsentLib#getCustomPurposeConsents}
     *  will also be able to be called from inside the callback.
     * @param c - Callback to be called when the user finishes interacting with the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentReady(ConsentLib.Callback c) {
        onConsentReady = c;
        return this;
    }

    /**
     * Called when the Dialog message is about to be shown
     * @param callback to be called when the message is ready to be displayed
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnMessageReady(ConsentLib.Callback callback) {
        onMessageReady = callback;
        return this;
    }

    /**
     *  <b>Optional</b> Sets a Callback to be called when something goes wrong in the WebView
     * @param callback called when something wrong happens in the webview
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnErrorOccurred(ConsentLib.Callback callback) {
        onErrorOccurred = callback;
        return this;
    }

    /**
     * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
     * campaigns. <b>Default:</b> false
     * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setStage(boolean st) {
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

    public ConsentLibBuilder enableNewPM(boolean newPM) {
        this.newPM = newPM;
        return this;
    }

    public ConsentLibBuilder setInAppMessagePageUrl(String inAppMessageUrl) {
        msgDomain = inAppMessageUrl;
        return this;
    }

    public ConsentLibBuilder setMmsDomain(String mmsDomain) {
        this.mmsDomain = mmsDomain;
        return this;
    }

    public ConsentLibBuilder setCmpDomain(String cmpDomain) {
        this.cmpDomain = cmpDomain;
        return this;
    }

    public ConsentLibBuilder setAuthId(String authId) throws ConsentLibException.BuildException {
        this.authId = new EncodedParam("authId", authId);
        return this;
    }

    public ConsentLibBuilder setShowPM(boolean isUserTriggered){
        this.isShowPM = isUserTriggered;
        return this;
    }

    public ConsentLibBuilder setTargetingParam(String key, Integer val)
            throws ConsentLibException.BuildException  {
        return setTargetingParam(key, (Object) val);
    }

    public ConsentLibBuilder setTargetingParam(String key, String val)
            throws ConsentLibException.BuildException {
        return setTargetingParam(key, (Object) val);
    }

    private ConsentLibBuilder setTargetingParam(String key, Object val) throws ConsentLibException.BuildException {
        try {
            this.targetingParams.put(key, val);
        } catch (JSONException e) {
            throw new ConsentLibException
                    .BuildException("error parsing targeting param, key: "+key+" value: "+val);
        }
        return this;
    }

    /**
     * <b>Optional</b> Sets the DEBUG level.
     * <i>(Not implemented yet)</i>
     * <b>Default</b>{@link ConsentLib.DebugLevel#DEBUG}
     * @param l - one of the values of {@link ConsentLib.DebugLevel#DEBUG}
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setDebugLevel(ConsentLib.DebugLevel l) {
        debugLevel = l;
        return this;
    }

    private void setTargetingParamsString() throws ConsentLibException {
        targetingParamsString = new EncodedParam("targetingParams", targetingParams.toString());
    }

    /**
     * The Android 4.x Browser throws an exception when parsing SourcePoint's javascript.
     * @return true if the API level is not supported
     */
    @SuppressWarnings("WeakerAccess")
    public boolean sdkNotSupported() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    /**
     * Run internal tasks and build the ConsentLib. This method will validate the
     * data coming from the previous Builders and throw {@link ConsentLibException.BuildException}
     * in case something goes wrong.
     * @return ConsentLib | ConsentLibNoOp
     * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
     */
    public ConsentLib build() throws ConsentLibException {
        if(sdkNotSupported()) {
            throw new ConsentLibException.BuildException(
                    "ConsentLib supports only API level 19 and above.\n"+
                            "See https://github.com/SourcePointUSA/android-cmp-app/issues/25 for more information."
            );
        }

        try {
            setTargetingParamsString();
        } catch (ConsentLibException e) {
            this.activity = null; // release reference to activity
            throw new ConsentLibException.BuildException(e.getMessage());
        }

        return new ConsentLib(this);
    }

    public ConsentLibBuilder setMessageTimeOut(long milliSecond){
        this.defaultMessageTimeOut = milliSecond;
        return this;
    }
}
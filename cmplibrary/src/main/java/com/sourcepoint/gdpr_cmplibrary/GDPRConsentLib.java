package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class GDPRConsentLib {

    private final String pmId;

    private final String PM_BASE_URL = "https://notice.sp-prod.net/privacy-manager/index.html";

    String metaData;
    String euConsent;

    public enum DebugLevel {DEBUG, OFF}


    public Boolean isSubjectToGdpr = null;

    public String consentUUID;

    public ConsentLibException error = null;

    public GDPRUserConsent userConsent;

    private static final String TAG = "GDPRConsentLib";

    Activity activity;
    final String property;
    final int accountId, propertyId;
    final OnConsentUIReadyCallback onConsentUIReady;
    final OnConsentUIFinishedCallback onConsentUIFinished;
    final OnConsentReadyCallback onConsentReady;
    final OnErrorCallback onError;
    final pmReadyCallback pmReady;
    final messageReadyCallback messageReady;
    final pmFinishedCallback pmFinished;
    final messageFinishedCallback messageFinished;
    final onActionCallback onAction;
    final boolean shouldCleanConsentOnError;

    public boolean isNative, isPmOn = false;

    private CountDownTimer mCountDownTimer;

    private final SourcePointClient sourcePoint;

    @SuppressWarnings("WeakerAccess")
    public ConsentWebView webView;

    public NativeMessage nativeView;

    public interface Callback {
        void run(GDPRConsentLib c);
    }

    public interface OnConsentUIReadyCallback {
        void run(View v);
    }

    public interface OnConsentUIFinishedCallback {

        void run(View v);
    }

    public interface OnConsentReadyCallback {
        void run(GDPRUserConsent c);
    }

    public interface OnErrorCallback {
        void run(ConsentLibException v);
    }

    public interface pmReadyCallback {
        void run();
    }

    public interface messageReadyCallback {
        void run();
    }

    public interface pmFinishedCallback {
        void run();
    }

    public interface messageFinishedCallback {
        void run();
    }

    public interface onActionCallback{
        void run(ActionTypes actionTypes);
    }

    public interface OnLoadComplete {
        void onSuccess(Object result);

        default void onFailure(ConsentLibException exception) {
            Log.d(TAG, "default implementation of onFailure, did you forget to override onFailure ?");
            exception.printStackTrace();
        }
    }

    private StoreClient storeClient;

    /**
     * @return a new instance of GDPRConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId, Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    GDPRConsentLib(ConsentLibBuilder b) {
        activity = b.activity;
        property = b.propertyConfig.propertyName;
        accountId = b.propertyConfig.accountId;
        propertyId = b.propertyConfig.propertyId;
        pmId = b.propertyConfig.pmId;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onConsentUIReady = b.onConsentUIReady;
        onConsentUIFinished = b.onConsentUIFinished;
        pmReady = b.pmReady;
        messageReady = b.messageReady;
        pmFinished = b.pmFinished;
        messageFinished = b.messageFinished;
        onAction = b.onAction;
        shouldCleanConsentOnError = b.shouldCleanConsentOnError;

        mCountDownTimer = b.getTimer(onCountdownFinished());

        sourcePoint = b.getSourcePointClient();

        storeClient = b.getStoreClient();
        setConsentData(b.authId);
    }

    private Runnable onCountdownFinished() {
        return () -> GDPRConsentLib.this.onErrorTask(new ConsentLibException("a timeout has occurred when loading the message"));
    }

    private void resetDataFields() {
        userConsent = new GDPRUserConsent();
        metaData = storeClient.DEFAULT_META_DATA;
        euConsent = storeClient.DEFAULT_EMPTY_CONSENT_STRING;
        consentUUID = null;
    }

    public void clearAllData() {
        resetDataFields();
        storeClient.clearAllData();
    }

    void setConsentData(String newAuthId) {

        if (didConsentUserChange(newAuthId, storeClient.getAuthId())) storeClient.clearAllData();

        euConsent = storeClient.getConsentString();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        try {
            userConsent = storeClient.getUserConsent();
        } catch (ConsentLibException e) {
            onErrorTask(e);
        }

        storeClient.setAuthId(newAuthId);

        storeClient.setCmpSdkID();

        storeClient.setCmpSdkVersion();
    }

    private boolean didConsentUserChange(String newAuthId, String oldAuthId) {
        return oldAuthId != null && newAuthId != null && !newAuthId.equals(oldAuthId);
    }

    ConsentWebView buildWebView() {
        return new ConsentWebView(activity) {

            @Override
            public void onConsentUIReady(boolean isFromPM) {
                showView(this , isFromPM);
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.onErrorTask(error);
            }

            @Override
            public void onAction(ConsentAction action) {
                GDPRConsentLib.this.onAction(action);
            }

            @Override
            public void onBackPressAction() {
                GDPRConsentLib.this.onAction(ConsentAction.getEmptyDismissAction(isPmOn));
            }
        };
    }

    public void onAction(ConsentAction action) {
        if (GDPRConsentLib.this.onAction != null)
            runOnLiveActivityUIThread( () ->GDPRConsentLib.this.onAction.run(action.actionType));

        try {
            Log.d(TAG, "onAction:  " + action.actionType + " + actionType");
            switch (action.actionType) {
                case SHOW_OPTIONS:
                    onShowOptions();
                    break;
                case PM_DISMISS:
                    onPmDismiss(action.requestFromPm);
                    break;
                case MSG_CANCEL:
                    onMsgCancel(action.requestFromPm);
                    break;
                default:
                    onDefaultAction(action);
                    break;
            }
        } catch (Exception e) {
            GDPRConsentLib.this.onErrorTask(new ConsentLibException(e, "Unexpected error when calling onAction."));
        }
    }

    private void setNativeMessageView(JSONObject msgJson) {
        runOnLiveActivityUIThread(() -> {
            try {
                nativeView.setCallBacks(this);
                nativeView.setAttributes(new NativeMessageAttrs(msgJson));
            } catch (ConsentLibException e) {
                onErrorTask(e);
            }
        });
    }

    public void onDefaultAction(ConsentAction action) {
        closeAllViews(action.requestFromPm);
        sendConsent(action);
    }

    public void onMsgCancel(boolean requestFromPM) {
        closeCurrentMessageView(requestFromPM);
        consentFinished();
    }

    protected void onPmDismiss(boolean requestFromPM) {
        isPmOn = false;
        webView.post(() -> {
            try{
                if (webView.canGoBack()) {
                    webView.goBack();
                    runPMFinished();
                }
                else onMsgCancel(requestFromPM);
            } catch(Exception e){
                onErrorTask(new ConsentLibException(e, "Error trying go back from consentUI."));
            }
        });
    }


    View getCurrentMessageView() {
        return isNative ? nativeView : webView;
    }

    public void onShowOptions() {
        showPm();
    }

    private void loadConsentUI(String url) {
        runOnLiveActivityUIThread(() -> {
            if (webView == null) webView = buildWebView();
            try {
                webView.loadConsentUIFromUrl(url);
            } catch (Exception e) {
                onErrorTask(new ConsentLibException(e, "Error trying to load url to webview: " + url));
            }
        });
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     *
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with GDPRConsentLib
     */
    public void run() {
        try {
            mCountDownTimer.start();
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    public void showPm() {
        try {
            mCountDownTimer.start();
            isPmOn = true;
            loadConsentUI(pmUrl());
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.showPm()"));
        }
    }

    public void run(NativeMessage v) {
        try {
            mCountDownTimer.start();
            nativeView = v;
            isNative = true;
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Error trying to load pm URL."));
        }
    }

    public void customConsentTo(
            ArrayList<String> vendors,
            ArrayList<String> categories,
            ArrayList<String> legIntCategories
    ) {
        customConsentTo(vendors, categories, legIntCategories, onConsentReady);
    }

    public void customConsentTo(
            ArrayList<String> vendors,
            ArrayList<String> categories,
            ArrayList<String> legIntCategories,
            OnConsentReadyCallback onCustomConsentReady
    ) {
        try {
            mCountDownTimer.start();
            sendCustomConsents(paramsToSendCustomConsents(vendors, categories, legIntCategories), onCustomConsentReady);
        } catch (ConsentLibException e) {
            onErrorTask(e);
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Error trying to send custom consents."));
        }
    }

    private void renderMsgAndSaveConsent() throws ConsentLibException {
        sourcePoint.getMessage(isNative, consentUUID, metaData, euConsent, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try {
                    JSONObject jsonResult = new JSONObject((String) result);
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    JSONObject jConsent = jsonResult.getJSONObject("userConsent");
                    jConsent.put("uuid", consentUUID);
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"));
                    storeData();
                    if (jsonResult.has("msgJSON") && !jsonResult.isNull("msgJSON")) {
                        setNativeMessageView(jsonResult.getJSONObject("msgJSON"));
                        showView(nativeView,false);
                    } else if(jsonResult.has("url") && !jsonResult.isNull("url")){
                        loadConsentUI(jsonResult.getString("url")+"&consentUUID="+consentUUID);
                    } else {
                        storeData();
                        consentFinished();
                    }
                } catch (Exception e) {
                    onErrorTask(new ConsentLibException(e, "Error trying to parse response from getConsents."));
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                onErrorTask(exception);
            }
        });
    }

    void showView(View view, boolean isFromPM) {
        mCountDownTimer.cancel();
        if (!hasParent(view)) {
            runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(view));
        }
        if (isFromPM) runPMReady();
        else runMessageReady();
    }

    private void runPMReady(){
        if (this.pmReady != null)
            runOnLiveActivityUIThread(GDPRConsentLib.this.pmReady::run);
        isPmOn = true;
    }

    private void runMessageReady(){
        if (this.messageReady != null)
            runOnLiveActivityUIThread(GDPRConsentLib.this.messageReady::run);
    }

    public void closeAllViews(boolean requestFromPM) {
        if (isNative) {
            closeView(nativeView,requestFromPM);
            if (isPmOn) closeView(webView,requestFromPM);
        } else closeView(webView ,requestFromPM);

    }

    private void closeCurrentMessageView(boolean requestFromPM) {
        closeView(getCurrentMessageView(),requestFromPM);
    }

    private boolean hasParent(View v) {
        return v != null && v.getParent() != null;
    }

    protected void closeView(View v, boolean requestFromPM) {
        if (hasParent(v)) {
            runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIFinished.run(v));
            if (requestFromPM) runPMFinished();
            else runMessageFinished();
        }
    }

    private void runPMFinished(){
        if (this.pmFinished != null)
            runOnLiveActivityUIThread(this.pmFinished::run);
    }

    private void runMessageFinished(){
        if (this.messageFinished != null)
            runOnLiveActivityUIThread(this.messageFinished::run);
    }

    private JSONObject paramsToSendConsent(ConsentAction action) throws ConsentLibException {
        try {

            Log.i("GDPR_UUID", "From sendConsentBody: " + consentUUID);

            JSONObject params = new JSONObject();
            params.put("accountId", accountId);
            params.put("propertyId", propertyId);
            params.put("propertyHref", "https://" + property);
            params.put("privacyManagerId", pmId);
            params.put("uuid", consentUUID);
            params.put("meta", metaData);
            params.put("actionType", action.actionType.code);
            params.put("requestFromPM", action.requestFromPm);
            params.put("choiceId", action.choiceId);
            params.put("pmSaveAndExitVariables", action.pmSaveAndExitVariables);
            return params;
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error trying to build body to send consents.");
        }
    }

    protected void sendConsent(ConsentAction action)  {
        try {
            sourcePoint.sendConsent(paramsToSendConsent(action), new OnLoadComplete() {
                @Override
                public void onSuccess(Object result) {
                    try {
                        JSONObject jsonResult = new JSONObject((String) result);
                        JSONObject jsonUserConsent = jsonResult.getJSONObject("userConsent");
                        euConsent = jsonUserConsent.getString("euconsent");
                        consentUUID = jsonResult.getString("uuid");
                        metaData = jsonResult.getString("meta");
                        userConsent = new GDPRUserConsent(jsonUserConsent, consentUUID);
                        storeData();
                        consentFinished();
                    } catch (Exception e) {
                        onErrorTask(new ConsentLibException(e, "Error trying to parse response from sendConsents."));
                    }
                }

                @Override
                public void onFailure(ConsentLibException exception) {
                    onErrorTask(exception);
                }
            });
        } catch (ConsentLibException e) {
            onErrorTask(e);
        }
    }

    private JSONObject paramsToSendCustomConsents(
            ArrayList<String> vendors,
            ArrayList<String> categories,
            ArrayList<String> legIntCategories
    ) throws ConsentLibException {
        try {
            JSONObject params = new JSONObject();
            params.put("consentUUID", consentUUID);
            params.put("propertyId", propertyId);
            params.put("vendors", new JSONArray(vendors));
            params.put("categories", new JSONArray(categories));
            params.put("legIntCategories", new JSONArray(legIntCategories));
            return params;
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error trying to build params to send custom consent");
        }
    }

    protected void sendCustomConsents(JSONObject params, OnConsentReadyCallback c) {
        try {
            sourcePoint.sendCustomConsents(params, new OnLoadComplete() {
                @Override
                public void onSuccess(Object result) {
                    try {
                        JSONObject jsonResult = new JSONObject((String) result);
                        userConsent = new GDPRUserConsent(fullConsentObj(jsonResult), consentUUID);
                        consentFinished(c);
                    } catch (Exception e) {
                        onErrorTask(new ConsentLibException(e, "Error trying to parse response from sendConsents."));
                    }
                }
                @Override
                public void onFailure(ConsentLibException e) {
                    onErrorTask(e);
                }
            });
        } catch (ConsentLibException e) {
            onErrorTask(e);
        }
    }

    private JSONObject fullConsentObj(JSONObject customConsent) throws JSONException, ConsentLibException {
        JSONObject fullObj = storeClient.getUserConsent().toJsonObject();
        fullObj.put("acceptedVendors", customConsent.getJSONArray("vendors"));
        fullObj.put("acceptedCategories", customConsent.getJSONArray("categories"));
        fullObj.put("specialFeatures", customConsent.getJSONArray("specialFeatures"));
        fullObj.put("legIntCategories", customConsent.getJSONArray("legIntCategories"));
        return fullObj;
    }


    String pmUrl() {
        HashSet<String> params = new HashSet<>();
        params.add("message_id=" + pmId);
        params.add("site_id="+ propertyId);
        if (consentUUID != null) params.add("consentUUID=" + consentUUID);

        return PM_BASE_URL + "?" + TextUtils.join("&", params);
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    void onErrorTask(ConsentLibException e) {
        this.error = e;
        if (shouldCleanConsentOnError) {
            storeClient.clearConsentData();
        }
        mCountDownTimer.cancel();
        closeCurrentMessageView(isPmOn);
        runOnLiveActivityUIThread(() -> {
            GDPRConsentLib.this.onError.run(e);
            releaseActivity();
        });
    }

    void storeData() throws JSONException, ConsentLibException {
        storeClient.setConsentUuid(consentUUID);
        storeClient.setMetaData(metaData);
        storeClient.setTCData(userConsent.TCData);
        storeClient.setConsentString(euConsent);
        storeClient.setUserConsents(userConsent);
    }

    void consentFinished(OnConsentReadyCallback c) {
        mCountDownTimer.cancel();
        runOnLiveActivityUIThread(() -> {
            c.run(userConsent);
            releaseActivity();
        });
    }

    public void releaseActivity(){
        activity = null;
    }

    void consentFinished() {
        consentFinished(onConsentReady);
    }
}
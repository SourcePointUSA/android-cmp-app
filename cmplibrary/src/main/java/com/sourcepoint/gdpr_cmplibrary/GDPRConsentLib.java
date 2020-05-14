package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

    public GDPRUserConsent userConsent = new GDPRUserConsent();

    private static final String TAG = "GDPRConsentLib";

    Activity activity;
    final String property;
    final int accountId, propertyId;
    final OnConsentUIReadyCallback onConsentUIReady;
    final OnConsentUIFinishedCallback onConsentUIFinished;
    final OnConsentReadyCallback onConsentReady;
    final OnErrorCallback onError;
    final boolean shouldCleanConsentOnError;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

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

        storeClient.setAuthId(newAuthId);
    }

    private boolean didConsentUserChange(String newAuthId, String oldAuthId) {
        return oldAuthId != null && newAuthId != null && !newAuthId.equals(oldAuthId);
    }

    private boolean hasLostInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }


    ConsentWebView buildWebView() {
        return new ConsentWebView(activity) {

            @Override
            public void onConsentUIReady() {
                showView(this);
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
        try {
            Log.d(TAG, "onAction:  " + action.actionType + " + actionType");
            switch (action.actionType) {
                case SHOW_OPTIONS:
                    onShowOptions();
                    break;
                case PM_DISMISS:
                    onPmDismiss();
                    break;
                case MSG_CANCEL:
                    onMsgCancel();
                    break;
                default:
                    onDefaultAction(action);
                    break;
            }
        } catch (Exception e) {
            GDPRConsentLib.this.onErrorTask(new ConsentLibException(e, "Unexpected error when calling onAction."));
        }
    }

    private void setNativeMessageView(JSONObject msgJson) throws ConsentLibException {
        nativeView.setCallBacks(this);
        nativeView.setAttributes(new NativeMessageAttrs(msgJson));
    }

    public void onDefaultAction(ConsentAction action) {
        closeAllViews();
        sendConsent(action);
    }

    public void onMsgCancel() {
        closeAllViews();
    }

    protected void onPmDismiss() {
        isPmOn = false;
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else closeView(webView);
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
            webView.loadConsentUIFromUrl(url);
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
            onMessageReadyCalled = false;
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            e.printStackTrace();
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    public void showPm() {
        try {
            mCountDownTimer.start();
            isPmOn = true;
            loadConsentUI(pmUrl());
        } catch (Exception e) {
            e.printStackTrace();
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
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"), consentUUID);
                    storeData();
                    if (jsonResult.has("msgJSON") && !jsonResult.isNull("msgJSON")) {
                        setNativeMessageView(jsonResult.getJSONObject("msgJSON"));
                        showView(nativeView);
                    } else if (jsonResult.has("url") && !jsonResult.isNull("url")) {
                        loadConsentUI(jsonResult.getString("url"));
                    } else {
                        storeData();
                        consentFinished();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onErrorTask(new ConsentLibException(e, "Error trying to parse response from getConsents."));
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                exception.printStackTrace();
                onErrorTask(exception);
            }
        });
    }

    void showView(View view) {
        mCountDownTimer.cancel();
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(view));
    }

    public void closeAllViews() {
        if (isNative) {
            closeView(nativeView);
            if (isPmOn) closeView(webView);
        } else closeView(webView);

    }

    private void closeCurrentMessageView() {
        closeView(getCurrentMessageView());
    }

    protected void closeView(View v) {
        if (v != null)
            runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIFinished.run(v));
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

    protected void sendConsent(ConsentAction action) {
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
                    Log.d(TAG, "Failed getting message response params.");
                    exception.printStackTrace();
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
                        userConsent = new GDPRUserConsent(fullConsentObj(jsonResult), consentUUID, storeClient.getTCData());
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

    private JSONObject fullConsentObj(JSONObject customConsent) throws JSONException {
        JSONObject fullObj = new JSONObject();
        fullObj.put("tcData", storeClient.getTCData());
        fullObj.put("euconsent", euConsent);
        fullObj.put("acceptedVendors", customConsent.getJSONArray("vendors"));
        fullObj.put("acceptedCategories", customConsent.getJSONArray("categories"));
        fullObj.put("specialFeatures", customConsent.getJSONArray("specialFeatures"));
        fullObj.put("legIntCategories", customConsent.getJSONArray("legIntCategories"));
        return fullObj;
    }


    String pmUrl() {
        HashSet<String> params = new HashSet<>();
        params.add("message_id=" + pmId);
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
        closeCurrentMessageView();
        runOnLiveActivityUIThread(() -> {
            GDPRConsentLib.this.onError.run(e);
            this.activity = null;
        });
    }

    void storeData() {
        storeClient.setConsentUuid(consentUUID);
        storeClient.setMetaData(metaData);
        storeClient.setTCData(userConsent.TCData);
        storeClient.setConsentString(euConsent);
    }

    void consentFinished(OnConsentReadyCallback c) {
        mCountDownTimer.cancel();
        runOnLiveActivityUIThread(() -> {
            c.run(userConsent);
            activity = null; // release reference to activity
        });
    }

    void consentFinished() {
        consentFinished(onConsentReady);
    }
}
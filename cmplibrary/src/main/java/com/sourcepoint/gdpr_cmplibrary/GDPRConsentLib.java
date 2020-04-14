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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;

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

    private String metaData;
    private String euConsent;

    public enum DebugLevel {DEBUG, OFF}


    public Boolean isSubjectToGdpr = null;

    public String consentUUID;

    public ConsentLibException error = null;

    public GDPRUserConsent userConsent = new GDPRUserConsent();

    private static final String TAG = "GDPRConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final OnConsentUIReadyCallback onConsentUIReady;
    private final OnConsentUIFinishedCallback onConsentUIFinished;
    private final OnConsentReadyCallback onConsentReady;
    private final OnErrorCallback onError;
    private final boolean shouldCleanConsentOnError;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

    public boolean isNative, isPmOn = false;

    private CountDownTimer mCountDownTimer = null;

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
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId , Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    GDPRConsentLib(ConsentLibBuilder b) {
        activity = b.activity;
        property = b.property;
        accountId = b.accountId;
        propertyId = b.propertyId;
        pmId = b.pmId;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onConsentUIReady = b.onConsentUIReady;
        onConsentUIFinished = b.onConsentUIFinished;
        shouldCleanConsentOnError = b.shouldCleanConsentOnError;

        // configurable time out
        defaultMessageTimeOut = b.messageTimeOut;

        sourcePoint = b.sourcePointClient;

        storeClient = b.storeClient;
        setConsentData(b.authId);

    }

    public void clearAllData(){
        storeClient.clearAllData();
    }

    private void setConsentData(String newAuthId){

        if(didAuthIdChange(newAuthId)) clearAllData();

        euConsent = storeClient.getConsentString();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        storeClient.setAuthId(newAuthId);
    }

    private boolean didAuthIdChange(String newAuthId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return !Objects.equals(newAuthId, storeClient.getAuthId());
        }
        //TODO: remove this code when we migrate to api > 19
        String storedAuthId = storeClient.getAuthId();
        if(newAuthId == null && storedAuthId == null) return false;
        else if (newAuthId != null && newAuthId.equals(storeClient.getAuthId())) return false;
        return true;
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

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity) {

            @Override
            public void onConsentUIReady() {
                cancelCounter();
                runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(this));
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.onErrorTask(error);
            }

            @Override
            public void onAction(ConsentAction action) {
                GDPRConsentLib.this.onAction(action);
            }
        };
    }

    public void onAction(ConsentAction action) {
        try{
            Log.d(TAG, "onAction:  " +  action.actionType  + " + actionType");
            switch (action.actionType) {
                case ActionTypes.SHOW_OPTIONS:
                    onShowOptions();
                    break;
                case ActionTypes.PM_DISMISS:
                    onPmDismiss();
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

    protected void onPmDismiss(){
        isPmOn = false;
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else closeView(webView);
            }
        });
    }

    private View getCurrentMessageView(){
        return isNative ? nativeView : webView;
    }

    public void onShowOptions(){
        loadPm();
    }

    private void loadPm() {
        loadConsentUI(pmUrl());
        isPmOn = true;
    }

    private void loadConsentUI(String url){
        if(webView == null) {
            webView = buildWebView();
            webView.loadConsentUIFromUrl(url);
        } else if(!isNative) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadConsentUIFromUrl(url);
                }
            });
        } else {
            showView(webView);
        }
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     *
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with GDPRConsentLib
     */
    public void run() {
        try {
            onMessageReadyCalled = false;
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            e.printStackTrace();
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    public void showPm() {
        loadPm();
    }

    public void run(NativeMessage v) {
        try {
            nativeView = v;
            isNative = true;
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Error trying to load pm URL."));
        }
    }

    private void renderMsgAndSaveConsent() throws ConsentLibException {
        sourcePoint.getMessage(isNative, consentUUID, metaData, euConsent, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"));
                    storeData();
                    if(jsonResult.has("msgJSON") && !jsonResult.isNull("msgJSON")) {
                        setNativeMessageView(jsonResult.getJSONObject("msgJSON"));
                        showView(nativeView);
                    } else if(jsonResult.has("url") && !jsonResult.isNull("url")){
                        loadConsentUI(jsonResult.getString("url"));
                    } else {
                        consentFinished();
                    }
                }
                catch (Exception e) {
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

    private void showView(View view){
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(view));
    }

    public void closeAllViews(){
        if(isNative){
            closeView(nativeView);
            if(isPmOn) closeView(webView);
        }
        else closeView(webView);

    }

    private void closeCurrentMessageView(){
        closeView(getCurrentMessageView());
    }

    protected void closeView(View v){
        if(v != null) runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIFinished.run(v));
    }

    private JSONObject paramsToSendConsent(ConsentAction action) throws ConsentLibException {
        try{

            Log.i("GDPR_UUID", "From sendConsentBody: " + consentUUID);

            JSONObject params = new JSONObject();
            params.put("accountId", accountId);
            params.put("propertyId", propertyId);
            params.put("propertyHref", "https://" + property);
            params.put("privacyManagerId", pmId);
            params.put("uuid", consentUUID);
            params.put("meta", metaData);
            params.put("actionType", action.actionType);
            params.put("requestFromPM", action.requestFromPm);
            params.put("choiceId", action.choiceId);
            params.put("pmSaveAndExitVariables", action.pmSaveAndExitVariables);
            return params;
        } catch(JSONException e){
            throw new ConsentLibException(e, "Error trying to build body to send consents.");
        }
    }

    protected void sendConsent(ConsentAction action) {
        try {
            sourcePoint.sendConsent(paramsToSendConsent(action), new OnLoadComplete() {
                @Override
                public void onSuccess(Object result) {
                    try{
                        JSONObject jsonResult = (JSONObject) result;
                        JSONObject jsonUserConsent = jsonResult.getJSONObject("userConsent");
                        userConsent = new GDPRUserConsent(jsonUserConsent);
                        euConsent = jsonUserConsent.getString("euconsent");
                        consentUUID = jsonResult.getString("uuid");
                        metaData = jsonResult.getString("meta");
                        Log.i("GDPR_UUID", "From sendConsentReponse: " + consentUUID);
                        consentFinished();
                    }
                    catch(Exception e){
                        Log.d(TAG, "Sorry, something went wrong");
                        e.printStackTrace();
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
            e.printStackTrace();
        }
    }


    private String pmUrl(){
        HashSet<String> params = new HashSet<>();
        params.add("message_id=" + pmId);
        if(consentUUID != null) params.add("consentUUID=" + consentUUID);

        return PM_BASE_URL + "?" + TextUtils.join("&", params);
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    private void onErrorTask(ConsentLibException e){
        this.error = e;
        if(shouldCleanConsentOnError) {
            storeClient.clearConsentData();
        }
        cancelCounter();
        closeCurrentMessageView();
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onError.run(e));
    }

    private void storeData(){
        storeClient.setConsentUuid(consentUUID);
        storeClient.setMetaData(metaData);
        storeClient.setTCData(userConsent.TCData);
        storeClient.setConsentString(euConsent);
    }

    private void cancelCounter(){
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }

    private void consentFinished() {
        storeData();
        runOnLiveActivityUIThread(() -> {
            if(userConsent != null) onConsentReady.run(userConsent);
            activity = null; // release reference to activity
        });
    }
}
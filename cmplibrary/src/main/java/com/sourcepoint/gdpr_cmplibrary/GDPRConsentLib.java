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

import com.google.common.annotations.VisibleForTesting;
import com.iab.gdpr_android.consent.VendorConsent;
import com.iab.gdpr_android.consent.VendorConsentDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;

import static com.sourcepoint.gdpr_cmplibrary.StoreClient.DEFAULT_EMPTY_CONSENT_STRING;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class GDPRConsentLib {

    private static final int MAX_PURPOSE_ID = 24;
    private final String pmId;

    private final String PM_BASE_URL = "https://gdpr-inapp-pm.sp-prod.net";

    private final String GDPR_ORIGIN = "https://gdpr-service.sp-prod.net";

    private String metaData;
    private String euConsent;

    public enum DebugLevel {DEBUG, OFF}


    public Boolean isSubjectToGdpr = null;

    public String consentUUID;

    public ConsentLibException error = null;

    public GDPRUserConsent userConsent;

    private static final String TAG = "GDPRConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final OnConsentUIReadyCallback onConsentUIReady;
    private final OnConsentUIFinishedCallback onConsentUIFinished;
    private final OnConsentReadyCallback onConsentReady;
    private final OnErrorCallback onError;
    private final boolean shouldCleanConsentOnError;

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

    public class ActionTypes {
        public static final int MSG_SHOW_OPTIONS = 12;
        public static final int MSG_REJECT = 13;
        public static final int MSG_ACCEPT = 11;
        public static final int MSG_CANCEL = 15;
        public static final int PM_COMPLETE = 1;
        public static final int PM_DISMISS = 2;
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

        mCountDownTimer = getTimer(b.defaultMessageTimeOut);

        sourcePoint = b.sourcePointClient;

        storeClient = b.storeClient;
        setConsentData(b.authId);


        setSubjectToGDPR();

    }

    public void clearAllData(){
        storeClient.clearAllData();
    }

    void setConsentData(String newAuthId){

        if(didConsentUserChange(newAuthId, storeClient.getAuthId())) storeClient.clearAllData();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        storeClient.setAuthId(newAuthId);
    }

    private boolean didConsentUserChange(String newAuthId, String oldAuthId){
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

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity) {

            @Override
            public void onConsentUIReady() {
                GDPRConsentLib.this.showView(this);
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.onErrorTask(error);
            }

            @Override
            public void onSavePM(GDPRUserConsent u) {
                GDPRConsentLib.this.userConsent = u;
                closeAllViews();
                sendConsent(ActionTypes.PM_COMPLETE, null);
            }

            @Override
            public void onAction(int choiceType, Integer choiceId) {
                GDPRConsentLib.this.onAction(choiceType, choiceId);
            }

            @Override
            public void onBackPressAction() {
                if (isPmOn){
                    GDPRConsentLib.this.onAction(ActionTypes.PM_DISMISS,null);
                }else {
                    GDPRConsentLib.this.onAction(ActionTypes.MSG_CANCEL, null);
                }
            }
        };
    }

    public void onAction(int choiceType, Integer choiceId) {
        try{
            Log.d(TAG, "onAction:  " +  choiceType  + " + choiceType");
            switch (choiceType) {
                case ActionTypes.MSG_SHOW_OPTIONS:
                    onMsgShowOptions();
                    break;
                case ActionTypes.MSG_ACCEPT:
                    onMsgAccepted(choiceId);
                    break;
                case ActionTypes.MSG_CANCEL:
                    onMsgCancel(choiceId);
                    break;
                case ActionTypes.MSG_REJECT:
                    onMsgRejected(choiceId);
                    break;
                case ActionTypes.PM_DISMISS:
                    onPmDismiss();
                default:
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

    public void onMsgAccepted(Integer choiceId) {
        closeAllViews();
        sendConsent(ActionTypes.MSG_ACCEPT, choiceId);
    }

    protected  void onPmDismiss(){
        isPmOn = false;
        goBackInAnotherThread(webView);
    }

    private void goBackInAnotherThread(ConsentWebView v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                if (v.canGoBack()) v.goBack();
                else closeView(v);
            }
        });
    }

    private View getCurrentMessageView(){
        return isNative ? nativeView : webView;
    }

    public void onMsgCancel(Integer choiceId){
        closeCurrentMessageView();
        consentFinished();
    }

    public void onMsgRejected(Integer choiceId) {
        closeAllViews();
        sendConsent(ActionTypes.MSG_REJECT, choiceId);
    }

    public void onMsgShowOptions(){
        loadPm();
    }

    private void loadPm() {
        isPmOn = true;
        loadConsentUI(pmUrl());
    }

    private void loadConsentUI(String url){
        mCountDownTimer.start();
        runOnLiveActivityUIThread(() -> {
            if(webView == null) webView = buildWebView();
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
        sourcePoint.getMessage(isNative, consentUUID, metaData, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = new JSONObject((String) result);
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    if(jsonResult.has("msgJSON") && !jsonResult.isNull("msgJSON")) {
                        setNativeMessageView(jsonResult.getJSONObject("msgJSON"));
                        showView(nativeView);
                    } else if(jsonResult.has("url") && !jsonResult.isNull("url")){
                        loadConsentUI(jsonResult.getString("url"));
                    } else {
                        userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"));
                        if(euConsent == null) euConsent = userConsent.consentString;
                        else userConsent.consentString = euConsent;
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
        cancelCounter();
        if(view.getParent() == null){
            runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(view));
        }
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

    private JSONObject paramsToSendConsent(int actionType, Integer choiceId) throws ConsentLibException {
        try{

            JSONObject params = new JSONObject();

            params.put("consents", userConsent != null ? userConsent.jsonConsents : null);
            params.put("accountId", accountId);
            params.put("propertyId", propertyId);
            params.put("propertyHref", "https://" + property);
            params.put("privacyManagerId", pmId);
            params.put("uuid", consentUUID);
            params.put("meta", metaData);
            params.put("actionType", actionType);
            params.put("requestFromPM", choiceId == null);
            params.put("choiceId", choiceId != null ? Integer.toString(choiceId) : null);
            return params;
        } catch(JSONException e){
            throw new ConsentLibException(e, "Error trying to build body to send consents.");
        }
    }

    protected void sendConsent(int actionType, Integer choiceId) {
        try {
            sourcePoint.sendConsent(paramsToSendConsent(actionType, choiceId), new OnLoadComplete() {
                @Override
                public void onSuccess(Object result) {
                    try{
                        JSONObject jsonResult = new JSONObject((String) result);
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

    private CountDownTimer getTimer(long defaultMessageTimeOut) {
        return new CountDownTimer(defaultMessageTimeOut, defaultMessageTimeOut) {
            @Override
            public void onTick(long millisUntilFinished) {     }
            @Override
            public void onFinish() {
                GDPRConsentLib.this.onErrorTask(new ConsentLibException("a timeout has occurred when loading the message"));
                cancel();
            }
        };
    }

    private String pmUrl(){
        HashSet<String> params = new HashSet<>();
        params.add("privacy_manager_id=" + pmId);
        params.add("site_id=" + propertyId);
        params.add("gdpr_origin=" + GDPR_ORIGIN);
        if(consentUUID != null) params.add("consentUUID=" + consentUUID);

        return PM_BASE_URL + "?" + TextUtils.join("&", params);
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    private void setSubjectToGDPR() {

        sourcePoint.getGDPRStatus(new OnLoadComplete() {
            @Override
            public void onSuccess(Object gdprApplies) {
                try {
                    JSONObject jsonResult = new JSONObject((String) gdprApplies);
                    isSubjectToGdpr = jsonResult.getBoolean("gdprApplies");
                } catch (JSONException e) {
                    onErrorTask(new ConsentLibException(e, "Error parsing gdprApplies result"));
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                onErrorTask(new ConsentLibException("Error getting gdprApplies from server."));
            }
        });
    }

    private void setIABVars() {

        if(euConsent == DEFAULT_EMPTY_CONSENT_STRING) return;

        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(euConsent);

        // Construct and save parsed purposes string
        char[] allowedPurposes = new char[MAX_PURPOSE_ID];
        for (int i = 0; i < MAX_PURPOSE_ID; i++) {
            allowedPurposes[i] = vendorConsent.isPurposeAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG, "allowedPurposes: " + new String(allowedPurposes));
        storeClient.setIabConsentParsedPurposeConsents(new String(allowedPurposes));


        // Construct and save parsed vendors string
        char[] allowedVendors = new char[vendorConsent.getMaxVendorId()];
        for (int i = 0; i < allowedVendors.length; i++) {
            allowedVendors[i] = vendorConsent.isVendorAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG, "allowedVendors: " + new String(allowedVendors));
        storeClient.setIabConsentParsedVendorConsents(new String(allowedVendors));
    }

    private void onErrorTask(ConsentLibException e){
        this.error = e;
        if(shouldCleanConsentOnError) {
            storeClient.clearIABConsentData();
        }
        cancelCounter();
        closeCurrentMessageView();
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onError.run(e));
    }

    private void storeData(){
        storeClient.setConsentSubjectToGDPr(isSubjectToGdpr);
        storeClient.setConsentUuid(consentUUID);
        storeClient.setIabConsentCmpPresent(true);
        storeClient.setIabConsentConsentString(euConsent);
        storeClient.setMetaData(metaData);
        setIABVars();
        storeClient.apply();
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
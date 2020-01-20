package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;


import com.iab.gdpr_android.consent.VendorConsent;
import com.iab.gdpr_android.consent.VendorConsentDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.UUID;

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

    public enum MESSAGE_OPTIONS {
        SHOW_PRIVACY_MANAGER,
        UNKNOWN
    }

    public Boolean isSubjectToGdpr = null;

    public String consentUUID;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    @SuppressWarnings("WeakerAccess")
    public MESSAGE_OPTIONS choiceType = null;

    public ConsentLibException error = null;

    public GDPRUserConsent userConsent;

    private static final String TAG = "GDPRConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final Callback onAction, onConsentReady, onError;
    private Callback onConsentUIReady, onConsentUIFinished;
    private final boolean shouldCleanConsentOnError;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

    private CountDownTimer mCountDownTimer = null;

    private final SourcePointClient sourcePoint;

    @SuppressWarnings("WeakerAccess")
    public ConsentWebView webView;

    public interface Callback {
        void run(GDPRConsentLib c);
    }

    public interface OnLoadComplete {
        void onSuccess(Object result);

        default void onFailure(ConsentLibException exception) {
            Log.d(TAG, "default implementation of onFailure, did you forget to override onFailure ?");
            exception.printStackTrace();
        }
    }

    public class ActionTypes {
        public static final int SHOW_PM = 12;
        public static final int MSG_REJECT = 13;
        public static final int MSG_ACCEPT = 11;
        public static final int DISMISS = 15;
        public static final int PM_COMPLETE = 1;
        public static final int PM_DISMMISS = 2;
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
        onAction = b.onAction;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onConsentUIReady = b.onConsentUIReady;
        onConsentUIFinished = b.onConsentUIFinished;
        shouldCleanConsentOnError = b.shouldCleanConsentOnError;


        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        sourcePoint = b.sourcePointClient;

        webView = buildWebView();
        storeClient = b.storeClient;

        euConsent = storeClient.getConsentString();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        setSubjectToGDPR();

    }

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity, defaultMessageTimeOut) {

            @Override
            public void onMessageReady() {
                Log.d("msgReady", "called");
                cancelCounter();
                if(!onMessageReadyCalled) {
                    runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIReady.run(GDPRConsentLib.this));
                    onMessageReadyCalled = true;
                }
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.onError(error);
            }

            @Override
            public void onSavePM(GDPRUserConsent u) {
                GDPRConsentLib.this.userConsent = u;
                sendConsent(ActionTypes.PM_COMPLETE);
            }

            @Override
            public void onAction(int choiceType) {
                try{
                    Log.d(TAG, "onAction:  " +  choiceType  + " + choiceType");
                    switch (choiceType) {
                        case ActionTypes.SHOW_PM:
                            GDPRConsentLib.this.choiceType = MESSAGE_OPTIONS.SHOW_PRIVACY_MANAGER;
                            onShowPm();
                            break;
                        case ActionTypes.MSG_ACCEPT:
                            onMsgAccepted();
                            break;
                        case ActionTypes.DISMISS:
                            onDismiss();
                            break;
                        case ActionTypes.MSG_REJECT:
                            onMsgRejected();
                            break;
                        case ActionTypes.PM_DISMMISS:
                            onPmDismiss();
                        default:
                            GDPRConsentLib.this.choiceType = MESSAGE_OPTIONS.UNKNOWN;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GDPRConsentLib.this.onError(new ConsentLibException(e, "Unexpected error when calling onAction."));
                }
            }
        };
    }

    private void onMsgAccepted() {
        sendConsent(ActionTypes.MSG_ACCEPT);
    }

    private  void onPmDismiss(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else consentFinished();
            }
        });
    }

    private void onDismiss(){
        consentFinished();
    }

    private void onMsgRejected() {
        sendConsent(ActionTypes.MSG_REJECT);
    }

    private void onShowPm(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(pmUrl());
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
            onMessageReadyCalled = false;
            mCountDownTimer = getTimer(defaultMessageTimeOut);
            mCountDownTimer.start();
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            e.printStackTrace();
            onError(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    public void showPm() {
        try {
            webView.loadConsentMsgFromUrl(pmUrl());
        } catch (Exception e) {
            e.printStackTrace();
            onError(new ConsentLibException(e, "Error trying to load pm URL."));
        }
    }

    private void renderMsgAndSaveConsent() throws ConsentLibException {
        if(webView == null) { webView = buildWebView(); }
        sourcePoint.getMessage(consentUUID, metaData, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"));
                    if(jsonResult.has("url")){
                        webView.loadConsentMsgFromUrl(jsonResult.getString("url"));
                    } else{
                        cancelCounter();
                        consentFinished();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    onError(new ConsentLibException(e, "Error trying to parse response from getConsents."));
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                exception.printStackTrace();
                onError(exception);
            }
        });
    }

    private JSONObject paramsToSendConsent(int actionType) throws ConsentLibException {
        try{

            Log.i("GDPR_UUID", "From sendConsentBody: " + consentUUID);

            JSONObject params = new JSONObject();

            params.put("consents", userConsent != null ? userConsent.jsonConsents : null);
            params.put("accountId", accountId);
            params.put("propertyId", propertyId);
            params.put("privacyManagerId", pmId);
            params.put("uuid", consentUUID);
            params.put("meta", metaData);
            params.put("actionType", actionType);
            params.put("requestFromPM", true);
            return params;
        } catch(JSONException e){
            throw new ConsentLibException(e, "Error trying to build body to send consents.");
        }
    }

    private void sendConsent(int actionType) {
        try {
            sourcePoint.sendConsent(paramsToSendConsent(actionType), new OnLoadComplete() {
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
                        onError(new ConsentLibException(e, "Error trying to parse response from sendConsents."));
                    }
                }

                @Override
                public void onFailure(ConsentLibException exception) {
                    Log.d(TAG, "Failed getting message response params.");
                    exception.printStackTrace();
                    onError(exception);
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
                if (!onMessageReadyCalled) {
                    onConsentUIReady = null;
                    GDPRConsentLib.this.onError(new ConsentLibException("a timeout has occurred when loading the message"));
                }
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
                isSubjectToGdpr = gdprApplies.equals("true");
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed setting the preference IAB_CONSENT_SUBJECT_TO_GDPR");
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

    private void onError(ConsentLibException e){
        this.error = e;
        if(shouldCleanConsentOnError) {
            storeClient.deleteIABConsentData();
        }
        cancelCounter();
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onError.run(GDPRConsentLib.this));
    }

    private void storeData(){
        storeClient.setConsentSubjectToGDPr(isSubjectToGdpr);
        storeClient.setConsentUuid(consentUUID);
        storeClient.setIabConsentCmpPresent(true);
        storeClient.setIabConsentConsentString(euConsent);
        storeClient.setMetaData(metaData);
        storeClient.setConsentUuid(consentUUID);
        setIABVars();
        storeClient.apply();
    }

    private void cancelCounter(){
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }

    private void consentFinished() {
        storeData();
        runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onConsentUIFinished.run(GDPRConsentLib.this));
        runOnLiveActivityUIThread(() -> {
            if(userConsent != null) onConsentReady.run(GDPRConsentLib.this);
            activity = null; // release reference to activity
        });
    }
}
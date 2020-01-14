package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_UUID_KEY = "consentUUID";

    @SuppressWarnings("WeakerAccess")
    public static final String META_DATA_KEY = "metaData";
    private final String pmId;

    private final String PM_BASE_URL = "https://ccpa-inapp-pm.sp-prod.net";

    private final String CCPA_ORIGIN = "https://ccpa-service.sp-prod.net";


    private String metaData;

    public enum DebugLevel {DEBUG, OFF}

    public enum MESSAGE_OPTIONS {
        SHOW_PRIVACY_MANAGER,
        UNKNOWN
    }

    public String consentUUID;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    @SuppressWarnings("WeakerAccess")
    public MESSAGE_OPTIONS choiceType = null;

    public ConsentLibException error = null;

    public UserConsent userConsent;

    private static final String TAG = "GDPRConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final ViewGroup viewGroup;
    private final Callback onAction, onConsentReady, onError;
    private Callback onMessageReady;
    private final EncodedParam encodedTargetingParams, encodedAuthId, encodedPMId;
    private final boolean weOwnTheView, isShowPM;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

    private CountDownTimer mCountDownTimer = null;

    private final SourcePointClient sourcePoint;

    private final SharedPreferences sharedPref;

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
    }

    /**
     * @return a new instance of GDPRConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId , Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    GDPRConsentLib(ConsentLibBuilder b) throws ConsentLibException.BuildException {
        activity = b.activity;
        property = b.property;
        accountId = b.accountId;
        propertyId = b.propertyId;
        encodedPMId = new EncodedParam("_sp_PMId",b.pmId);
        pmId = b.pmId;
        isShowPM = b.isShowPM;
        encodedAuthId = b.authId;
        onAction = b.onAction;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onMessageReady = b.onMessageReady;
        encodedTargetingParams = b.targetingParamsString;
        viewGroup = b.viewGroup;

        weOwnTheView = viewGroup != null;
        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        sourcePoint = new SourcePointClient(b.accountId, b.property + "/" + b.page, propertyId, b.staging);

        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);
        metaData = sharedPref.getString(META_DATA_KEY, null);
        webView = buildWebView();
    }

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity, defaultMessageTimeOut, isShowPM) {

            @Override
            public void onMessageReady() {
                onMessageReadyCalled = true;
                Log.d("msgReady", "called");
                if (mCountDownTimer != null) mCountDownTimer.cancel();
                runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onMessageReady.run(GDPRConsentLib.this));
                displayWebViewIfNeeded();
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.error = error;
                runOnLiveActivityUIThread(() -> GDPRConsentLib.this.onError.run(GDPRConsentLib.this));
            }

            @Override
            public void onSavePM(UserConsent u) {
                GDPRConsentLib.this.userConsent = u;
                try {
                    sendConsent();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                        default:
                            GDPRConsentLib.this.choiceType = MESSAGE_OPTIONS.UNKNOWN;
                            break;
                    }
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void onMsgAccepted() throws UnsupportedEncodingException, JSONException {
        userConsent = new UserConsent(UserConsent.ConsentStatus.acceptedAll);
        sendConsent();
    }

    private void onDismiss(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else finish();
            }
        });
    }

    private void onMsgRejected() throws UnsupportedEncodingException, JSONException {
        userConsent = new UserConsent(UserConsent.ConsentStatus.acceptedNone);
        sendConsent();
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
    public void run() throws ConsentLibException.NoInternetConnectionException {
        onMessageReadyCalled = false;
        mCountDownTimer = getTimer(defaultMessageTimeOut);
        mCountDownTimer.start();
        renderMsgAndSaveConsent();
    }

    public void showPm() {
        webView.loadUrl(pmUrl());
    }

    private void renderMsgAndSaveConsent() throws ConsentLibException.NoInternetConnectionException {
        if(webView == null) { webView = buildWebView(); }
        sourcePoint.getMessage(consentUUID, metaData, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    userConsent = new  UserConsent(jsonResult.getJSONObject("userConsent"));
                    if(jsonResult.has("url")){
                        webView.loadConsentMsgFromUrl(jsonResult.getString("url"));
                    }else{
                        finish();
                    }
                }
                //TODO call onFailure callbacks / throw consentlibException
                catch(JSONException e){
                    Log.d(TAG, "Failed reading message response params.");
                }
                catch(ConsentLibException e){
                    Log.d(TAG, "Sorry, no internet connection");
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting message response params.");
            }
        });
    }

    private JSONObject paramsToSendConsent() throws JSONException {
        JSONObject params = new JSONObject();

        params.put("consents", userConsent.jsonConsents);
        params.put("accountId", accountId);
        params.put("propertyId", propertyId);
        params.put("privacyManagerId", pmId);
        params.put("uuid", consentUUID);
        params.put("meta", metaData);
        return params;
    }

    private void sendConsent() throws JSONException, UnsupportedEncodingException {
        sourcePoint.sendConsent(paramsToSendConsent(), new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    consentUUID = jsonResult.getString("uuid");
                    finish();
                }
                //TODO call onFailure callbacks / throw consentlibException
                catch(JSONException e){
                    Log.d(TAG, "Failed reading message response params.");
                }
                catch(Exception e){
                    Log.d(TAG, "Sorry, something went wrong");
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting message response params.");
            }
        });
    }

    private CountDownTimer getTimer(long defaultMessageTimeOut) {
        return new CountDownTimer(defaultMessageTimeOut, defaultMessageTimeOut) {
            @Override
            public void onTick(long millisUntilFinished) {     }
            @Override
            public void onFinish() {
                if (!onMessageReadyCalled) {
                    onMessageReady = null;
                    webView.onError(new ConsentLibException("a timeout has occurred when loading the message"));
                }
            }
        };
    }

    private String pmUrl(){
        HashSet<String> params = new HashSet<>();
        params.add("privacy_manager_id=" + pmId);
        params.add("site_id=" + propertyId);
        params.add("ccpa_origin=" + CCPA_ORIGIN);
        if(consentUUID != null) params.add("ccpaUUID=" + consentUUID);

        return PM_BASE_URL + "?" + TextUtils.join("&", params);
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    private void displayWebViewIfNeeded() {
        if (weOwnTheView) {
            runOnLiveActivityUIThread(() -> {
                if (webView != null) {
                    if (webView.getParent() != null) {
                        ((ViewGroup) webView.getParent()).removeView(webView);
                    }
                    webView.display();
                    viewGroup.addView(webView);
                }
            });
        }
    }

    private void removeWebViewIfNeeded() {
        if (weOwnTheView && activity != null) destroy();
    }


    private void storeData(){
        //Log.i("uuid", consentUUID);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        if(consentUUID != null) editor.putString(CONSENT_UUID_KEY, consentUUID);
        if(metaData != null) editor.putString(META_DATA_KEY, metaData);
        editor.commit();
    }

    private void finish() {
        storeData();
        Log.i("uuid", consentUUID);
        runOnLiveActivityUIThread(() -> {
            removeWebViewIfNeeded();
            if(userConsent != null) onConsentReady.run(GDPRConsentLib.this);
            activity = null; // release reference to activity
        });
    }

    public void destroy() {
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        if (webView != null) {
            if (viewGroup != null) {
                viewGroup.removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
    }
}
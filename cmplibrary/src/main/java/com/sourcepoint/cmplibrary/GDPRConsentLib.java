package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.os.CountDownTimer;
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

    public UserConsent userConsent;

    private static final String TAG = "GDPRConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final ViewGroup viewGroup;
    private final Callback onAction, onConsentReady, onError;
    private Callback onMessageReady;
    private final EncodedParam encodedTargetingParams, encodedAuthId, encodedPMId;
    private final boolean weOwnTheView, isShowPM, isStage;

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
        isStage = b.staging;

        weOwnTheView = viewGroup != null;
        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        sourcePoint = new SourcePointClient(b.accountId, b.property + "/" + b.page, propertyId, b.stagingCampaign, b.staging);

        webView = buildWebView();
        storeClient = b.storeClient;

        euConsent = storeClient.getConsentString();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        Log.i("***uuid - init", consentUUID != null ? consentUUID : "no_consent");

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
                    sendConsent(ActionTypes.PM_COMPLETE);
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
                        case ActionTypes.PM_DISMMISS:
                            onPmDismiss();
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
        sendConsent(ActionTypes.MSG_ACCEPT);
    }

    private  void onPmDismiss(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else consentReady();
            }
        });
    }

    private void onDismiss(){
        consentReady();
    }

    private void onMsgRejected() throws UnsupportedEncodingException, JSONException {
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
    public void run() throws ConsentLibException.NoInternetConnectionException {
        onMessageReadyCalled = false;
        mCountDownTimer = getTimer(defaultMessageTimeOut);
        mCountDownTimer.start();
        try {
            renderMsgAndSaveConsent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showPm() {
        webView.loadUrl(pmUrl());
    }

    private void renderMsgAndSaveConsent() throws UnsupportedEncodingException, JSONException {
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
                        consentReady();
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

    private JSONObject paramsToSendConsent(int actionType) throws JSONException {
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
    }

    private void sendConsent(int actionType) throws JSONException, UnsupportedEncodingException {
        sourcePoint.sendConsent(paramsToSendConsent(actionType), new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    JSONObject jsonUserConsent = jsonResult.getJSONObject("userConsent");
                    userConsent = new UserConsent(jsonUserConsent);
                    euConsent = jsonUserConsent.getString("euconsent");
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    consentReady();
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
        params.add("gdpr_origin=" + GDPR_ORIGIN);
        if(consentUUID != null) params.add("consentUUID=" + consentUUID);

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
        Log.i("***uuid - stored", consentUUID);
        storeClient.setConsentSubjectToGDPr(isSubjectToGdpr);
        storeClient.setConsentUuid(consentUUID);
        storeClient.setIabConsentCmpPresent(true);
        storeClient.setIabConsentConsentString(euConsent);
        storeClient.setMetaData(metaData);
        storeClient.setIabConsentParsedPurposeConsents("");
        storeClient.setIabConsentParsedVendorConsents("");
        storeClient.commit();
    }

    private void consentReady() {
        storeData();
        //Log.i("uuid", consentUUID);
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
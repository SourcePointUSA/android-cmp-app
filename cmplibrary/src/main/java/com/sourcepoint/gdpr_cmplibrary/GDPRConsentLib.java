package com.sourcepoint.gdpr_cmplibrary;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
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

    final String PM_BASE_URL = "https://notice.sp-prod.net/privacy-manager/index.html";
    final String OTT_PM_BASE_URL = "https://notice.sp-prod.net/privacy-manager-ott/index.html";
    private final onBeforeSendingConsent onBeforeSendingConsent;
    private final OnNoIntentActivitiesFound onNoIntentActivitiesFound;

    String metaData;
    String euConsent;

    public Boolean isSubjectToGdpr = null;

    public String consentUUID;

    public ConsentLibException error = null;

    public GDPRUserConsent userConsent;
    private static final String TAG = "GDPRConsentLib";

    UIThreadHandler uiThreadHandler;


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
    boolean isOTT;

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

    public interface OnNoIntentActivitiesFound {
        void run(String url);
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

    public interface onBeforeSendingConsent {
        void run(ConsentAction a, OnBeforeSendingConsentComplete c);
    }

    public interface OnLoadComplete extends ProneToFailure {
        void onSuccess(Object result);
    }

    public class OnBeforeSendingConsentComplete implements ProneToFailure {
        public void post(ConsentAction a){
            GDPRConsentLib.this.sendConsent(a);
        };

        @Override
        public void onFailure(ConsentLibException exception) {
            GDPRConsentLib.this.onErrorTask(exception);
        }
    }

    public interface ProneToFailure {

        default void onFailure(ConsentLibException exception) {
            Log.d(TAG, "default implementation of onFailure, did you forget to override onFailure ?");
            exception.printStackTrace();
        }
    }

    private StoreClient storeClient;

    /**
     * @return a new instance of GDPRConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId, Context context) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, context);
    }

    GDPRConsentLib(ConsentLibBuilder b) {
        uiThreadHandler = b.getUIThreadHandler();
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
        onBeforeSendingConsent = b.onBeforeSendingConsent;
        onNoIntentActivitiesFound = b.onNoIntentActivitiesFound;
        this.isOTT = b.isOTT;

        //TODO: inject consoleWebview from the builder as well (overload/callbacks refactor required)
        webView = buildWebView(b.getContext());

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
            userConsent = new GDPRUserConsent();
        }

        storeClient.setAuthId(newAuthId);

        storeClient.setCmpSdkID();

        storeClient.setCmpSdkVersion();
    }

    private boolean didConsentUserChange(String newAuthId, String oldAuthId) {
        return oldAuthId != null && newAuthId != null && !newAuthId.equals(oldAuthId);
    }

    ConsentWebView buildWebView(Context context) {
        return new ConsentWebView(context) {

            @Override
            public void onConsentUIReady(boolean isFromPM) {
                showView(this , isFromPM);
            }

            @Override
            public void onError(ConsentLibException error) {
                GDPRConsentLib.this.onErrorTask(error);
            }

            @Override
            public void onNoIntentActivitiesFoundFor(String url) {
                uiThreadHandler.postIfEnabled(() -> onNoIntentActivitiesFound.run(url));
            }

            @Override
            public void onAction(ConsentAction action) {
                GDPRConsentLib.this.onAction(action);
            }

        };
    }

    public void onAction(ConsentAction action) {
        try {
            uiThreadHandler.postIfEnabled( () ->GDPRConsentLib.this.onAction.run(action.actionType));
            Log.d(TAG, "onAction:  " + action.actionType + " + actionType");
            switch (action.actionType) {
                case SHOW_OPTIONS:
                    onShowOptions(action);
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
        uiThreadHandler.post(() -> {
            try {
                nativeView.setCallBacks(this);
                nativeView.setAttributes(new NativeMessageAttrs(msgJson));
            } catch (ConsentLibException e) {
                onErrorTask(e);
            } catch (Exception e) {
                onErrorTask(new ConsentLibException(e, "Unexpected error trying to setNativeMsg attributes"));
            }
        });
    }

    public void onDefaultAction(ConsentAction action) {
        closeAllViews(action.requestFromPm);
        onBeforeSendingConsent.run(action, new OnBeforeSendingConsentComplete());
    }

    public void onMsgCancel(boolean requestFromPM) {
        try {
            closeCurrentMessageView(requestFromPM);
            consentFinished();
        } catch (ConsentLibException e) {
            onErrorTask(e);
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Unexpect error on cancel action."));
        }
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

    public void onShowOptions(ConsentAction action) {
        showPm(action.privacyManagerId, action.pmTab);
    }

    private void loadConsentUI(String url) {
        uiThreadHandler.postIfEnabled(() -> {
            try {
                webView.loadConsentUIFromUrl(url);
            } catch(ConsentLibException e) {
                onErrorTask(e);
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
        } catch (ConsentLibException e) {
            onErrorTask(e);
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    public void showPm() {
        showPm(null,null);
    }

    public void showPm(String privacyManagerId, String pmTab) {
        try {
            mCountDownTimer.start();
            isPmOn = true;
            loadConsentUI(pmUrl(privacyManagerId, pmTab));
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
        } catch (ConsentLibException e) {
            onErrorTask(e);
        } catch (Exception e) {
            onErrorTask(new ConsentLibException(e, "Unexpected error trying to run Native Message"));
        }
    }

    public void customConsentTo(
            Collection<String> vendors,
            Collection<String> categories,
            Collection<String> legIntCategories
    ) {
        customConsentTo(vendors, categories, legIntCategories, onConsentReady);
    }

    public void customConsentTo(
            Collection<String> vendors,
            Collection<String> categories,
            Collection<String> legIntCategories,
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
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"), consentUUID);
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
                } catch (ConsentLibException e) {
                    onErrorTask(e);
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
            uiThreadHandler.postIfEnabled(() -> GDPRConsentLib.this.onConsentUIReady.run(view));
        }
        if (isFromPM) runPMReady();
        else runMessageReady();
    }

    private void runPMReady(){
        uiThreadHandler.postIfEnabled(GDPRConsentLib.this.pmReady::run);
        isPmOn = true;
    }

    private void runMessageReady(){
        uiThreadHandler.postIfEnabled(GDPRConsentLib.this.messageReady::run);
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
            uiThreadHandler.postIfEnabled(() -> GDPRConsentLib.this.onConsentUIFinished.run(v));
            if (requestFromPM) runPMFinished();
            else runMessageFinished();
        }
    }

    private void runPMFinished(){
        uiThreadHandler.postIfEnabled(this.pmFinished::run);
    }

    private void runMessageFinished(){
        uiThreadHandler.postIfEnabled(this.messageFinished::run);
    }

    private JSONObject paramsToSendConsent(ConsentAction action) throws ConsentLibException {
        try {
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
            params.put("pubData", action.getPubData());
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
                    } catch (ConsentLibException e) {
                        onErrorTask(e);
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
            Collection<String> vendors,
            Collection<String> categories,
            Collection<String> legIntCategories
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
        fullObj.put("grants", customConsent.getJSONObject("grants"));
        return fullObj;
    }


    String pmUrl(String privacyManagerId, String pmTab) {
        HashSet<String> params = new HashSet<>();
        params.add("message_id=" + (privacyManagerId != null ? privacyManagerId : pmId));
        if (pmTab != null)
        params.add("pmTab="+pmTab);
        params.add("site_id="+ propertyId);
        if (consentUUID != null) params.add("consentUUID=" + consentUUID);
        String PM_URL = isOTT ? OTT_PM_BASE_URL : PM_BASE_URL;
        return PM_URL + "?" + TextUtils.join("&", params);
    }

    void onErrorTask(ConsentLibException e) {
        this.error = e;
        if (shouldCleanConsentOnError) {
            storeClient.clearConsentData();
        }
        mCountDownTimer.cancel();
        closeCurrentMessageView(isPmOn);
        uiThreadHandler.postIfEnabled(() -> GDPRConsentLib.this.onError.run(e));
        destroy();
    }

    void storeData() throws JSONException, ConsentLibException {
        storeClient.setConsentUuid(consentUUID);
        storeClient.setMetaData(metaData);
        storeClient.setTCData(userConsent.TCData);
        storeClient.setConsentString(euConsent);
        storeClient.setUserConsents(userConsent);
    }

    void consentFinished(OnConsentReadyCallback c) throws JSONException, ConsentLibException {
        mCountDownTimer.cancel();
        storeData();
        uiThreadHandler.postIfEnabled(() -> c.run(userConsent));
        destroy();
    }

    void consentFinished() throws JSONException, ConsentLibException {
        consentFinished(onConsentReady);
    }

    void destroy(){
        uiThreadHandler.post(webView::destroy);
        uiThreadHandler.disable();
    }
}
package com.sourcepoint.gdpr_cmplibrary;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sourcepoint.gdpr_cmplibrary.exception.*;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class GDPRConsentLib implements IGDPRConsentLib{
    static final String PM_BASE_URL = "https://cdn.privacy-mgmt.com/privacy-manager/index.html";
    static final String OTT_PM_BASE_URL = "https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html";

    private final String pmId;
    private final onBeforeSendingConsent onBeforeSendingConsent;
    private final OnNoIntentActivitiesFound onNoIntentActivitiesFound;
    private final Context context;

    String metaData;
    String euConsent;

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
    String messageLanguage;
    String privacyManagerTab;

    public boolean isNative, isPmOn = false;

    private final CountDownTimer mCountDownTimer;

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
        }

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

    final StoreClient storeClient;
    private final Logger logger;

    /**
     * @return a new instance of GDPRConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId, Context context) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, context);
    }

    GDPRConsentLib(ConsentLibBuilder b) {
        context = b.getContext();
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
        messageLanguage = b.messageLanguage;
        privacyManagerTab = b.pmTab;
        isOTT = b.isOTT;

        logger = b.getLogger(accountId, propertyId);
        uiThreadHandler = b.getUIThreadHandler();
        mCountDownTimer = b.getTimer(onCountdownFinished());
        sourcePoint = b.getSourcePointClient();
        storeClient = b.getStoreClient();

        // :warning: following methods depend on storeClient initialization
        setConsentData(b.authId);
    }

    private Runnable onCountdownFinished() {
        return () -> {
            logger.error(new GenericSDKException("a timeout has occurred when loading the message"));
            GDPRConsentLib.this.onErrorTask(new ConsentLibException("a timeout has occurred when loading the message"));
        };
    }

    private void resetDataFields() {
        userConsent = new GDPRUserConsent(logger);
        metaData = StoreClient.DEFAULT_META_DATA;
        euConsent = StoreClient.DEFAULT_EMPTY_CONSENT_STRING;
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
            userConsent = new GDPRUserConsent(logger);
        }

        storeClient.setAuthId(newAuthId);

        storeClient.setCmpSdkID();

        storeClient.setCmpSdkVersion();
    }

    boolean didConsentUserChange(String newAuthId, String oldAuthId) {
        return oldAuthId != null && newAuthId != null && !newAuthId.equals(oldAuthId);
    }

    ConsentWebView buildWebView(Context context) throws Exception {
        if(webView == null) webView = new ConsentWebView(context) {
            @Override
            public void onConsentUIReady(boolean isFromPM) {
                showView(this, isFromPM);
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

            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
        return webView;
    }

    public void onAction(ConsentAction action) {
        try {
            uiThreadHandler.postIfEnabled( () ->GDPRConsentLib.this.onAction.run(action.actionType));
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
            logger.error(new InvalidOnActionEventPayloadException("Unexpected error when calling onAction."));
            GDPRConsentLib.this.onErrorTask(new ConsentLibException(e, "Unexpected error when calling onAction."));
        }
    }

    private void setNativeMessageView(JSONObject msgJson) {
        uiThreadHandler.post(() -> {
            try {
                nativeView.setCallBacks(this);
                nativeView.setAttributes(new NativeMessageAttrs(msgJson, logger));
            } catch (ConsentLibException e) {
                onErrorTask(e);
            } catch (Exception e) {
                logger.error(new InvalidResponseNativeMessageException("Unexpected error trying to setNativeMsg attributes"));
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
            logger.error(new InvalidResponseWebMessageException("Unexpect error on cancel action."));
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
                logger.error(new InvalidEventPayloadException("Error trying go back from consentUI."));
                onErrorTask(new ConsentLibException(e, "Error trying go back from consentUI."));
            }
        });
    }


    View getCurrentMessageView() {
        return isNative ? nativeView : webView;
    }

    public void onShowOptions(ConsentAction action) {
        String privacyManagerId = action.privacyManagerId == null ? pmId : action.privacyManagerId;
        String pmTab = action.pmTab == null ? privacyManagerTab : action.pmTab;
        showPm(privacyManagerId, pmTab);
    }

    void loadConsentUI(String url) {
        uiThreadHandler.postIfEnabled(() -> {
            try {
                buildWebView(context).loadConsentUIFromUrl(url);
            } catch(ConsentLibException e) {
                onErrorTask(e);
            } catch (Exception e) {
                logger.error(new WebViewException("Error trying to load url to webview: " + url));
                onErrorTask(new ConsentLibException(e, "Error trying to load url to webview: " + url));
            }
        });
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     */
    @Override
    public void run() {
        try {
            mCountDownTimer.start();
            renderMsgAndSaveConsent();
        } catch (ConsentLibException e) {
            onErrorTask(e);
        } catch (Exception e) {
            logger.error(new GenericSDKException(e, "Unexpected error on consentLib.run()"));
            onErrorTask(new ConsentLibException(e, "Unexpected error on consentLib.run()"));
        }
    }

    @Override
    public void showPm() {
        // if privacyManagerTab has not been set, use null value.
        String selectedTab = TextUtils.isEmpty(privacyManagerTab) ? null :  privacyManagerTab;
        showPm(pmId,selectedTab);
    }

    public void showPm(@NotNull String privacyManagerId, String pmTab) {
        try {
            mCountDownTimer.start();
            isPmOn = true;
            String url = pmUrl(privacyManagerId, pmTab);
            loadConsentUI(url);
        } catch (Exception e) {
            logger.error(new WebViewException(e, "Unexpected error on consentLib.showPm()"));
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
            logger.error(new InvalidResponseNativeMessageException(e, "Unexpected error trying to run Native Message"));
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
            logger.error(new InvalidResponseCustomConsent(e, "Error trying to send custom consents."));
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
                    userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"), consentUUID, logger);
                    storeData();
                    if (jsonResult.has("msgJSON") && !jsonResult.isNull("msgJSON")) {
                        setNativeMessageView(jsonResult.getJSONObject("msgJSON"));
                        showView(nativeView,false);
                    } else if(jsonResult.has("url") && !jsonResult.isNull("url")){
                        loadConsentUI(jsonResult.getString("url")+"&consentUUID=" + consentUUID +
                                getSelectedMessageLanguage());
                    } else {
                        storeData();
                        consentFinished();
                    }
                } catch (ConsentLibException e) {
                    onErrorTask(e);
                } catch (Exception e) {
                    logger.error(new InvalidResponseConsentException(e, "Error trying to parse response from getConsents."));
                    onErrorTask(new ConsentLibException(e, "Error trying to parse response from getConsents."));
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                onErrorTask(exception);
            }
        });
    }

    private String getSelectedMessageLanguage(){
        String consentLanguage = "";
        if (!TextUtils.isEmpty(messageLanguage))
            consentLanguage = messageLanguage;
        return "&consentLanguage="+consentLanguage;
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
            String consentLanguage = action.consentLanguage != null ? action.consentLanguage : Locale.getDefault().getLanguage().toUpperCase();
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
            params.put("consentLanguage", consentLanguage);
            return params;
        } catch (JSONException e) {
            logger.error(new InvalidLocalDataException(e, "Error trying to build body to send consents."));
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
                        userConsent = new GDPRUserConsent(jsonUserConsent, consentUUID, logger);
                        storeData();
                        consentFinished();
                    } catch (ConsentLibException e) {
                        onErrorTask(e);
                    } catch (Exception e) {
                        logger.error(new InvalidResponseConsentException(e, "Error trying to parse response from sendConsents."));
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
            logger.error(new InvalidLocalDataException(e, "Error trying to build params to send custom consent"));
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
                        userConsent = new GDPRUserConsent(fullConsentObj(jsonResult), consentUUID, logger);
                        consentFinished(c);
                    } catch (Exception e) {
                        if(!(e instanceof ConsentLibException)){
                            logger.error(new InvalidLocalDataException(e, "Error trying to parse response from sendConsents."));
                        }
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


    String pmUrl(@NotNull String privacyManagerId, String pmTab) {
        HashSet<String> params = new HashSet<>();
        params.add("message_id=" + privacyManagerId);
        if (pmTab != null) params.add("pmTab="+pmTab);
        params.add("site_id="+ propertyId);
        if (consentUUID != null) params.add("consentUUID=" + consentUUID);
        String consentLanguage = messageLanguage != null ? messageLanguage : "";
        params.add("consentLanguage=" + consentLanguage);
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
        uiThreadHandler.post(() -> {
            //WebView is null in case of error on buildWebView() method
            if(webView != null) webView.destroy();
        });
        uiThreadHandler.disable();
    }
}

package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;

import com.iab.gdpr_android.consent.VendorConsent;
import com.iab.gdpr_android.consent.VendorConsentDecoder;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashSet;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents as well as IAB consent purposes.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class ConsentLib {
    /**
     * If the user has consent data stored, reading for this key in the shared preferences will return true
     */
    @SuppressWarnings("WeakerAccess")
    public static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";

    /**
     * If the user is subject to GDPR, reading for this key in the shared preferences will return "1" otherwise "0"
     */
    @SuppressWarnings("WeakerAccess")
    public static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";

    /**
     * They key used to store the IAB Consent string for the user in the shared preferences
     */
    @SuppressWarnings("WeakerAccess")
    public static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    /**
     * They key used to read and write the parsed IAB Purposes consented by the user in the shared preferences
     */
    @SuppressWarnings("WeakerAccess")
    public static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";

    /**
     * They key used to read and write the parsed IAB Vendor consented by the user in the shared preferences
     */
    @SuppressWarnings("WeakerAccess")
    public static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";

    @SuppressWarnings("WeakerAccess")
    public static final String EU_CONSENT_KEY = "euconsent";

    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_UUID_KEY = "consentUUID";

    public enum DebugLevel {DEBUG, OFF}

    public enum MESSAGE_OPTIONS {
        SHOW_PRIVACY_MANAGER,
        UNKNOWN
    }

    public String euconsent, consentUUID;

    private static final int MAX_PURPOSE_ID = 24;

    private Boolean shouldCleanConsentOnError;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    @SuppressWarnings("WeakerAccess")
    public MESSAGE_OPTIONS choiceType = null;

    public ConsentLibException error = null;

    private static final String TAG = "ConsentLib";
    private static final String SP_PREFIX = "_sp_";
    private static final String SP_PROPERTY_ID = SP_PREFIX + "property_id";
    private final static String CUSTOM_CONSENTS_KEY = SP_PREFIX + "_custom_consents";

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
        void run(ConsentLib c);
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
        public static final int MSG_DISMISS = 15;
        public static final int PM_COMPLETE = 99;
        public static final int PM_CANCEL = 98;
    }

    /**
     * @return a new instance of ConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId , Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    ConsentLib(ConsentLibBuilder b) throws ConsentLibException.BuildException {
        activity = b.activity;
        property = b.property;
        accountId = b.accountId;
        propertyId = b.propertyId;
        encodedPMId = new EncodedParam("_sp_PMId",b.pmId);
        isShowPM = b.isShowPM;
        encodedAuthId = b.authId;
        onAction = b.onAction;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onMessageReady = b.onMessageReady;
        encodedTargetingParams = b.targetingParamsString;
        viewGroup = b.viewGroup;
        shouldCleanConsentOnError = b.shouldCleanConsentOnError;

        weOwnTheView = viewGroup != null;
        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        sourcePoint = new SourcePointClientBuilder(b.accountId, b.property + "/" + b.page, propertyId, b.staging)
                .setStagingCampaign(b.stagingCampaign)
                .setShowPM(b.isShowPM)
                .setCmpDomain(b.cmpDomain)
                .setMessageDomain(b.msgDomain)
                .setMmsDomain(b.mmsDomain)
                .build();

        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        euconsent = sharedPref.getString(EU_CONSENT_KEY, null);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);

        webView = buildWebView();
    }

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity, defaultMessageTimeOut, isShowPM) {
            private boolean isDefined(String s) {
                return s != null && !s.equals("undefined") && !s.isEmpty();
            }

            @Override
            public void onMessageReady() {
                onMessageReadyCalled = true;
                Log.d("msgReady", "called");
                if (mCountDownTimer != null) mCountDownTimer.cancel();
                runOnLiveActivityUIThread(() -> ConsentLib.this.onMessageReady.run(ConsentLib.this));
                displayWebViewIfNeeded();
            }

            @Override
            public void onError(ConsentLibException error) {
                ConsentLib.this.error = error;
                if(shouldCleanConsentOnError) {
                    clearAllConsentData();
                }
                runOnLiveActivityUIThread(() -> ConsentLib.this.onError.run(ConsentLib.this));
                ConsentLib.this.finish();
            }

            @Override
            public void onConsentReady(String euConsent, String consentUUID) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isDefined(euConsent)) {
                    ConsentLib.this.euconsent = euConsent;
                    editor.putString(EU_CONSENT_KEY, euConsent);
                }
                if (isDefined(consentUUID)) {
                    ConsentLib.this.consentUUID = consentUUID;
                    editor.putString(CONSENT_UUID_KEY, consentUUID);
                    Log.d("Consent UUID = ", consentUUID);
                }
                if (isDefined(euConsent) && isDefined(consentUUID)) {
                    editor.apply();
                    setIABVars(euConsent);
                }
                ConsentLib.this.finish();
            }

            @Override
            public void onAction(int choiceId, int choiceType) {
                Log.d(TAG, "onAction: choiceId:" + choiceId + "choiceType: " + choiceType);
                //noinspection SwitchStatementWithTooFewBranches
                switch (choiceType) {
                    case ActionTypes.SHOW_PM:
                        ConsentLib.this.choiceType = MESSAGE_OPTIONS.SHOW_PRIVACY_MANAGER;
                        onShowPm();
                        break;
                    case ActionTypes.MSG_ACCEPT:
                        onMsgAccepted();
                        break;
                    case ActionTypes.MSG_DISMISS:
                        onMsgClosed();
                        break;
                    case ActionTypes.MSG_REJECT:
                        onMsgRejected();
                        break;
                    case ActionTypes.PM_CANCEL:
                        onPmCanceled();
                        break;
                    case ActionTypes.PM_COMPLETE:
                        onPmCompleted();
                        break;
                    default:
                        ConsentLib.this.choiceType = MESSAGE_OPTIONS.UNKNOWN;
                        break;
                }
//                runOnLiveActivityUIThread(() -> ConsentLib.this.onAction.run(ConsentLib.this));
//                onConsentReady("", "");
            }
        };
    }

    private void onMsgAccepted(){
        //TODO acceptAll()
        ConsentLib.this.finish();
    }

    private void onMsgClosed(){
        ConsentLib.this.finish();
    }

    private void onMsgRejected(){
        //TODO rejectAll()
        ConsentLib.this.finish();
    }

    private void onPmCanceled(){
        ConsentLib.this.finish();
    }

    private void onPmCompleted(){
        //saveConsent(sourcePoint);
        ConsentLib.this.finish();
    }

    private void onShowPm(){
        webView.loadPM();
    }

    private boolean isDefined(String s) {
        return s != null && !s.equals("undefined") && !s.isEmpty();
    }

    private void saveConsent(String euConsent, String consentUUID){
        SharedPreferences.Editor editor = sharedPref.edit();
        if (isDefined(euConsent)) {
            ConsentLib.this.euconsent = euConsent;
            editor.putString(EU_CONSENT_KEY, euConsent);
        }
        if (isDefined(consentUUID)) {
            ConsentLib.this.consentUUID = consentUUID;
            editor.putString(CONSENT_UUID_KEY, consentUUID);
            Log.d("Consnet UUID = ", consentUUID);
        }
        if (isDefined(euConsent) && isDefined(consentUUID)) {
            editor.apply();
            setIABVars(euConsent);
        }
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     * The Following keys should will be available in the shared preferences storage after this method
     * is called:
     * <ul>
     * <li>{@link ConsentLib#IAB_CONSENT_CMP_PRESENT}</li>
     * <li>{@link ConsentLib#IAB_CONSENT_SUBJECT_TO_GDPR}</li>
     * </ul>
     *
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with ConsentLib
     */
    public void run() throws ConsentLibException.NoInternetConnectionException {
        onMessageReadyCalled = false;
        renderMessage();
        setSharedPreference(IAB_CONSENT_CMP_PRESENT, true);
        setSubjectToGDPR();
    }

    private void renderMessage() throws ConsentLibException.NoInternetConnectionException {
        if(webView == null) { webView = buildWebView(); }
        sourcePoint.getMessage(new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    String  msgUrl = ((JSONObject) result).getString("url");
                    webView.loadConsentMsgFromUrl(msgUrl);
                    mCountDownTimer = getTimer(defaultMessageTimeOut);
                    mCountDownTimer.start();
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

    private void setSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }


    @SuppressWarnings("SameParameterValue")
    private void setSharedPreference(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /*call this method to clear sharedPreferences from app onError*/
    public void clearAllConsentData(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(IAB_CONSENT_CMP_PRESENT);
        editor.remove(IAB_CONSENT_CONSENT_STRING);
        editor.remove(IAB_CONSENT_PARSED_PURPOSE_CONSENTS);
        editor.remove(IAB_CONSENT_PARSED_VENDOR_CONSENTS);
        editor.remove(IAB_CONSENT_SUBJECT_TO_GDPR);
        editor.remove(CONSENT_UUID_KEY);
        editor.remove(EU_CONSENT_KEY);
        editor.remove(CUSTOM_CONSENTS_KEY);
        editor.commit();
    }

    private void setSubjectToGDPR() {

        sourcePoint.getGDPRStatus(new OnLoadComplete() {
            @Override
            public void onSuccess(Object gdprApplies) {
                setSharedPreference(IAB_CONSENT_SUBJECT_TO_GDPR, gdprApplies.equals("true") ? "1" : "0");
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed setting the preference IAB_CONSENT_SUBJECT_TO_GDPR");
            }
        });
    }

    private void setIABVars(String euconsent) {
        setSharedPreference(IAB_CONSENT_CONSENT_STRING, euconsent);

        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(euconsent);

        // Construct and save parsed purposes string
        char[] allowedPurposes = new char[MAX_PURPOSE_ID];
        for (int i = 0; i < MAX_PURPOSE_ID; i++) {
            allowedPurposes[i] = vendorConsent.isPurposeAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG, "allowedPurposes: " + new String(allowedPurposes));
        setSharedPreference(IAB_CONSENT_PARSED_PURPOSE_CONSENTS, new String(allowedPurposes));

        // Construct and save parsed vendors string
        char[] allowedVendors = new char[vendorConsent.getMaxVendorId()];
        for (int i = 0; i < allowedVendors.length; i++) {
            allowedVendors[i] = vendorConsent.isVendorAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG, "allowedVendors: " + new String(allowedVendors));
        setSharedPreference(IAB_CONSENT_PARSED_VENDOR_CONSENTS, new String(allowedVendors));
    }

    /**
     * This method receives an Array of Strings representing the custom vendor ids you want to get
     * the consents for and a callback.<br/>
     * The callback will be called with an Array of booleans once the data is ready. If the element
     * <i>i</i> of this array is <i>true</i> it means the user has consented to the vendor index <i>i</i>
     * from the customVendorIds parameter. Otherwise it will be <i>false</i>.
     *
     * @param callback        - callback that will be called with an array of boolean indicating if the user has given consent or not to those vendors.
     */
    public void getCustomVendorConsents(final OnLoadComplete callback) {
        loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object result) {
                HashSet<Consent> consents = (HashSet<Consent>) result;
                HashSet<CustomVendorConsent> vendorConsents = new HashSet<>();
                for (Consent consent : consents) {
                    if (consent instanceof CustomVendorConsent) {
                        vendorConsents.add((CustomVendorConsent) consent);
                    }
                }
                callback.onSuccess(vendorConsents);
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting custom vendor consents.");
                callback.onFailure(exception);
            }
        });
    }

    /**
     * This method receives a callback which is called with an Array of all the purposes ({@link Consent}) the user has given consent for.
     *
     * @param callback called with an array of {@link Consent}
     */
    public void getCustomPurposeConsents(final OnLoadComplete callback) {
        loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object result) {
                HashSet<Consent> consents = (HashSet<Consent>) result;
                HashSet<CustomPurposeConsent> purposeConsents = new HashSet<>();
                for (Consent consent : consents) {
                    if (consent instanceof CustomPurposeConsent) {
                        purposeConsents.add((CustomPurposeConsent) consent);
                    }
                }
                callback.onSuccess(purposeConsents);
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting custom purpose consents.");
                callback.onFailure(exception);
            }
        });
    }

    /**
     * Given a list of IAB vendor IDs, returns a corresponding array of boolean each representing
     * the consent was given or not to the requested vendor.
     *
     * @param vendorIds an array of standard IAB vendor IDs.
     * @return an array with same size as vendorIds param representing the results in the same order.
     * @throws ConsentLibException if the consent is not dialog completed or the
     *                             consent string is not present in SharedPreferences.
     */
    @SuppressWarnings("unused")
    public boolean[] getIABVendorConsents(int[] vendorIds) throws ConsentLibException {
        final VendorConsent vendorConsent = getParsedConsentString();
        boolean[] results = new boolean[vendorIds.length];

        for (int i = 0; i < vendorIds.length; i++) {
            results[i] = vendorConsent.isVendorAllowed(vendorIds[i]);
        }
        return results;
    }

    /**
     * Given a list of IAB Purpose IDs, returns a corresponding array of boolean each representing
     * the consent was given or not to the requested purpose.
     *
     * @param purposeIds an array of standard IAB purpose IDs.
     * @return an array with same size as purposeIds param representing the results in the same order.
     * @throws ConsentLibException if the consent dialog is not completed or the
     *                             consent string is not present in SharedPreferences.
     */
    @SuppressWarnings("unused")
    public boolean[] getIABPurposeConsents(int[] purposeIds) throws ConsentLibException {
        final VendorConsent vendorConsent = getParsedConsentString();
        boolean[] results = new boolean[purposeIds.length];

        for (int i = 0; i < purposeIds.length; i++) {
            results[i] = vendorConsent.isPurposeAllowed(purposeIds[i]);
        }
        return results;
    }

    private String getConsentStringFromPreferences() throws ConsentLibException {
        final String euconsent = sharedPref.getString(IAB_CONSENT_CONSENT_STRING, null);
        if (euconsent == null) {
            throw new ConsentLibException("Could not find consent string in sharedUserPreferences.");
        }
        return euconsent;
    }

    private VendorConsent getParsedConsentString() throws ConsentLibException {
        final String euconsent = getConsentStringFromPreferences();
        VendorConsent parsedConsent;
        try {
            parsedConsent = VendorConsentDecoder.fromBase64String(euconsent);
        } catch (Exception e) {
            throw new ConsentLibException("Unable to parse raw string \"" + euconsent + "\" into consent string.");
        }
        return parsedConsent;
    }

    /**
     * When we receive data from the server, if a given custom vendor is no longer given consent
     * to, its information won't be present in the payload. Therefore we have to first clear the
     * preferences then set each vendor to true based on the response.
     */
    private void clearStoredVendorConsents(final String[] customVendorIds, SharedPreferences.Editor editor) {
        for (String vendorId : customVendorIds) {
            editor.remove(CUSTOM_CONSENTS_KEY + vendorId);
        }
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    private void loadAndStoreCustomVendorAndPurposeConsents(final String[] vendorIds, final OnLoadComplete callback) {
        final String propertyIdKey = SP_PROPERTY_ID + "_" + accountId + "_" + property;
        String propertyID = Integer.toString(propertyId);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(propertyIdKey, propertyID);
        editor.apply();
        sourcePoint.getCustomConsents(consentUUID, euconsent, propertyID, vendorIds, new OnLoadComplete() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object result) {
                HashSet<Consent> consents = (HashSet<Consent>) result;
                HashSet<String> consentStrings = new HashSet<>();
                SharedPreferences.Editor editor = sharedPref.edit();
                clearStoredVendorConsents(vendorIds, editor);
                for (Consent consent : consents) {
                    consentStrings.add(consent.toJSON().toString());
                }
                editor.putStringSet(CUSTOM_CONSENTS_KEY, consentStrings);
                editor.apply();
                callback.onSuccess(consents);
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void displayWebViewIfNeeded() {
        if (weOwnTheView) {
            runOnLiveActivityUIThread(() -> {
                if (webView != null) {
                    if (webView.getParent() != null) {
                        ((ViewGroup) webView.getParent()).removeView(webView); // <- fix
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

    private void finish() {
        runOnLiveActivityUIThread(() -> {
            removeWebViewIfNeeded();
            onConsentReady.run(ConsentLib.this);
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

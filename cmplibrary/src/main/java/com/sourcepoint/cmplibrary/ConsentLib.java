package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.consent.VendorConsentDecoder;

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
    public static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";

    /**
     * If the user is subject to GDPR, reading for this key in the shared preferences will return "1" otherwise "0"
     */
    public static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";

    /**
     * They key used to store the IAB Consent string for the user in the shared preferences
     */
    public static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    /**
     * They key used to read and write the parsed IAB Purposes consented by the user in the shared preferences
     */
    public static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";

    /**
     * They key used to read and write the parsed IAB Vendor consented by the user in the shared preferences
     */
    public static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";

    public enum DebugLevel {
        DEBUG,
//        INFO,
//        TIME,
//        WARN,
//        ERROR,
        OFF
    }

    // visible for grabbing consent from shared preferences
    public static final String EU_CONSENT_KEY = "euconsent";
    public static final String CONSENT_UUID_KEY = "consentUUID";
    private static final int MAX_PURPOSE_ID = 24;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    public Integer choiceType = null;

    public String euconsent;
    public String consentUUID;

    private static final String TAG = "ConsentLib";

    private static final String SP_PREFIX = "_sp_";
    private static final String SP_SITE_ID = SP_PREFIX + "site_id";

    private final static String CUSTOM_CONSENTS_KEY = SP_PREFIX + "_custom_consents";

    private Activity activity;
    private final String siteName;
    private final int accountId;
    private final ViewGroup viewGroup;
    private final Callback onMessageChoiceSelect;
    private final Callback onInteractionComplete;
    private final EncodedParam encodedTargetingParams;
    private final EncodedParam encodedDebugLevel;

    private final SourcePointClient sourcePoint;

    private final SharedPreferences sharedPref;

    private android.webkit.CookieManager cm;

    private WebView webView;

    public interface Callback {
        void run(ConsentLib c);
    }

    /**
     * First step in building ConsentLib
     */
    public interface ActivityStep {
        /**
         *  Sets the activity in which the consent WebView will be loaded into.
         *  This method has to be called first, right after newBuilder()
         * @param a - the activity in which the consent WebView will be loaded into.
         * @return ConsentLib.AccountIdStep
         */
        AccountIdStep setActivity(Activity a);
    }

    /**
     * Second step in building ConsentLib
     */
    public interface AccountIdStep {
        /**
         *  Sets the account id that will be used to communicate with SourcePoint.
         *  This method has to be called right after setActivity
         * @param id - can be found in the Publisher's portal -> Account.
         * @return SiteNameStep - the next build step
         * @see SiteNameStep
         */
        SiteNameStep setAccountId(int id);
    }

    /**
     * Third step in building ConsentLib
     */
    public interface SiteNameStep {
        /**
         *  Sets the site name used to retrieve campaigns, scenarios, etc, from SourcePoint.
         *  This method has to be called right after setAccountId
         * @param s - the site name, just copy and past from the publisher's portal
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        BuildStep setSiteName(String s);
    }

    public interface OnLoadComplete {
        void onSuccess(Object result);

        default void onFailure(ConsentLibException exception) {
            Log.d("ConsentLib", "default implementation of onFailure called with Exception: "+exception);
        }
    }

    /**
     * Optional steps in building ConsentLib
     */
    public interface BuildStep {
        /**
         *  <b>Optional</b> Sets the page name in which the WebView was shown. Used for logging only.
         * @param s - a string representing page, e.g "/home"
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        BuildStep setPage(String s);

        /**
         *  <b>Optional</b> Sets the view group in which WebView will will be rendered into.
         *  If it's not called or called with null, the MainView will be used instead.
         *  In case the main view is not a ViewGroup, a BuildException will be thrown during
         *  when build() is called.
         * @param v - the view group
         * @return BuildStep - the next build step
         * @see BuildStep
         * @see ConsentLibException.BuildException
         * @see build
         */
        BuildStep setViewGroup(ViewGroup v);

        // TODO: add what are the possible choices returned to the Callback
        /**
         *  <b>Optional</b> Sets the Callback to be called when the user selects an option on the WebView.
         *  The selected choice will be available in the instance variable ConsentLib.choiceType
         * @param c - a callback that will be called when the user selects an option on the WebView
         * @return BuildStep - the next build step
         * @see BuildStep
         * @see Callback
         */
        BuildStep setOnMessageChoiceSelect(Callback c);

        /**
         *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
         *  either by closing it, canceling or accepting the terms.
         *  At this point, the following keys will available populated in the sharedStorage:
         *  <ul>
         *      <li>{@link ConsentLib#EU_CONSENT_KEY}</li>
         *      <li>{@link ConsentLib#CONSENT_UUID_KEY}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_SUBJECT_TO_GDPR}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_CONSENT_STRING}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_PURPOSE_CONSENTS}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_VENDOR_CONSENTS}</li>
         *  </ul>
         *  Also at this point, the methods {@link ConsentLib#getCustomVendorConsents},
         *  {@link ConsentLib#getCustomPurposeConsents}
         *  will also be able to be called from inside the callback.
         * @param c - Callback to be called when the user finishes interacting with the WebView
         * @return BuildStep - the next build step
         * @see BuildStep
         * @see Callback
         */
        BuildStep setOnInteractionComplete(Callback c);

        /**
         * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
         * campaigns. <b>Default:</b> false
         * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        BuildStep setStage(boolean st);

        /**
         * <b>Optional</b> This parameter refers to SourcePoint's environment itself. True for staging
         * or false for production.
         * <b>Default:</b> false
         * @param st - True for staging or false for production
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        BuildStep setInternalStage(boolean st);

        BuildStep setInAppMessagePageUrl(String pageUrl);

        BuildStep setMmsDomain(String mmsDomain);

        BuildStep setCmpDomain(String cmpDomain);

        BuildStep setTargetingParam(String key, Integer val) throws ConsentLibException.BuildException ;

        BuildStep setTargetingParam(String key, String val) throws ConsentLibException.BuildException ;

        /**
         * <b>Optional</b> Sets the DEBUG level.
         * <i>(Not implemented yet)</i>
         * <b>Default</b>{@link DebugLevel#DEBUG}
         * @param l - one of the values of {@link DebugLevel#DEBUG}
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        BuildStep setDebugLevel(DebugLevel l);

        /**
         * Run internal tasks and build the ConsentLib. This method will validate the
         * data coming from the previous BuildSteps and throw {@link ConsentLibException.BuildException}
         * in case something goes wrong.
         * @return ConsentLib
         * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
         */
        ConsentLib build() throws ConsentLibException;
    }

    private static class Builder implements ActivityStep, AccountIdStep, SiteNameStep, BuildStep {
        private Activity activity;
        private int accountId;
        private String siteName;
        private String page = "";
        private ViewGroup viewGroup = null;
        private final Callback noOpCallback = new Callback() { @Override public void run(ConsentLib c) { } };
        private Callback onMessageChoiceSelect = noOpCallback;
        private Callback onInteractionComplete = noOpCallback;
        private boolean staging, stagingCampaign = false;
        private String mmsDomain, cmpDomain, msgDomain = null;
        private final JSONObject targetingParams = new JSONObject();
        private EncodedParam targetingParamsString = null;
        private DebugLevel debugLevel = DebugLevel.OFF;

        /**
         *  Sets the activity in which ConsentLib will get its context from.
         *  This method has to be called first, right after newBuilder()
         * @param a - the activity in which the consent WebView will be loaded into.
         * @return AccountIdStep - the next build step
         * @see AccountIdStep
         */
        public AccountIdStep setActivity(Activity a) {
            activity = a;
            return this;
        }

        /**
         *  Sets the account id that will be used to communicate with SourcePoint.
         *  Your id can be found in the Publisher's portal -> Account.
         *  This method has to be called right after setActivity
         * @param id - can be found in the Publisher's portal -> Account.
         * @return SiteNameStep - the next build step
         * @see SiteNameStep
         */
        public SiteNameStep setAccountId(int id) {
            accountId = id;
            return this;
        }

        /**
         *  Sets the site name used to retrieve campaigns, scenarios, etc, from SourcePoint.
         *  This method has to be called right after setAccountId
         * @param s - the site name, just copy and past from the publisher's portal
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setSiteName(String s) {
            siteName = s;
            return this;
        }

        /**
         *  <b>Optional</b> Sets the page name in which the WebView was shown. Used for logging only.
         * @param p - a string representing page, e.g "/home"
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setPage(String p) {
            page = p;
            return this;
        }

        /**
         *  <b>Optional</b> Sets the view group in which WebView will will be rendered into.
         *  If it's not called or called with null, the MainView will be used instead.
         *  In case the main view is not a ViewGroup, a BuildException will be thrown during
         *  when build() is called.
         * @param v - the view group
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setViewGroup(ViewGroup v) {
            viewGroup = v;
            return this;
        }

        // TODO: add what are the possible choices returned to the Callback
        /**
         *  <b>Optional</b> Sets the Callback to be called when the user selects an option on the WebView.
         *  The selected choice will be available in the instance variable ConsentLib.choiceType
         * @param c - a callback that will be called when the user selects an option on the WebView
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setOnMessageChoiceSelect(Callback c) {
            onMessageChoiceSelect = c;
            return this;
        }

        /**
         *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
         *  either by closing it, canceling or accepting the terms.
         *  At this point, the following keys will available populated in the sharedStorage:
         *  <ul>
         *      <li>{@link ConsentLib#EU_CONSENT_KEY}</li>
         *      <li>{@link ConsentLib#CONSENT_UUID_KEY}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_SUBJECT_TO_GDPR}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_CONSENT_STRING}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_PURPOSE_CONSENTS}</li>
         *      <li>{@link ConsentLib#IAB_CONSENT_PARSED_VENDOR_CONSENTS}</li>
         *  </ul>
         *  Also at this point, the methods {@link ConsentLib#getCustomVendorConsents},
         *  {@link ConsentLib#getCustomPurposeConsents}
         *  will also be able to be called from inside the callback.
         * @param c - Callback to be called when the user finishes interacting with the WebView
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setOnInteractionComplete(Callback c) {
            onInteractionComplete = c;
            return this;
        }

        /**
         * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
         * campaigns. <b>Default:</b> false
         * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setStage(boolean st) {
            stagingCampaign = st;
            return this;
        }

        /**
         * <b>Optional</b> This parameter refers to SourcePoint's environment itself. True for staging
         * or false for production. <b>Default:</b> false
         * @param st - True for staging or false for production
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setInternalStage(boolean st) {
            staging = st;
            return this;
        }

        public BuildStep setInAppMessagePageUrl(String inAppMessageUrl) {
            msgDomain = inAppMessageUrl;
            return this;
        }

        public BuildStep setMmsDomain(String mmsDomain) {
            this.mmsDomain = mmsDomain;
            return this;
        }

        public BuildStep setCmpDomain(String cmpDomain) {
            this.cmpDomain = cmpDomain;
            return this;
        }

        // TODO: document these.
        public BuildStep setTargetingParam(String key, Integer val)
                throws ConsentLibException.BuildException  {
            return setTargetingParam(key, (Object) val);
        }

        public BuildStep setTargetingParam(String key, String val)
                throws ConsentLibException.BuildException {
            return setTargetingParam(key, (Object) val);
        }

        private BuildStep setTargetingParam(String key, Object val) throws ConsentLibException.BuildException {
            try {
                this.targetingParams.put(key, val);
            } catch (JSONException e) {
                throw new ConsentLibException()
                        .new BuildException("error parsing targeting param, key: "+key+" value: "+val);
            }
            return this;
        }

        /**
         * <b>Optional</b> Sets the DEBUG level.
         * <i>(Not implemented yet)</i>
         * <b>Default</b>{@link DebugLevel#DEBUG}
         * @param l - one of the values of {@link DebugLevel#DEBUG}
         * @return BuildStep - the next build step
         * @see BuildStep
         */
        public BuildStep setDebugLevel(DebugLevel l) {
            debugLevel = l;
            return this;
        }

        private void setTargetingParamsString() throws ConsentLibException {
            targetingParamsString = new EncodedParam("targetingParams", targetingParams.toString());
        }

        private void isRequired(String attrName, Object value) throws ConsentLibException.BuildException {
            if(value == null) { throw new ConsentLibException().new BuildException(attrName + " is missing"); }
        }

        private void validate() throws ConsentLibException.BuildException {
            isRequired("activity", activity);
            isRequired("account Id", accountId);
            isRequired("site name", siteName);
        }

        private void setDefaults () throws ConsentLibException.BuildException {
            if (viewGroup == null) {
                // render on top level activity view if no viewGroup specified
                View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                if (view instanceof ViewGroup) {
                    viewGroup = (ViewGroup) view;
                } else {
                    throw new ConsentLibException().new BuildException("Current window is not a ViewGroup, can't render WebView");
                }
            }
        }

        /**
         * Run internal tasks and build the ConsentLib. This method will validate the
         * data coming from the previous BuildSteps and throw {@link ConsentLibException.BuildException}
         * in case something goes wrong.
         * @return ConsentLib
         * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
         */
        public ConsentLib build() throws ConsentLibException {
            try {
                setDefaults();
                setTargetingParamsString();
                validate();
            } catch (ConsentLibException e) {
                this.activity = null; // release reference to activity
                throw new ConsentLibException().new BuildException(e.getMessage());
            }

            return new ConsentLib(this);
        }
    }

    private void flushOrSyncCookies() {
        // forces the cookies sync between RAM and local storage
        // https://developer.android.com/reference/android/webkit/CookieSyncManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }

    @SuppressWarnings("unused")
    private class MessageInterface {
        // called when message loads, brings the WebView to the front when the message is ready
        @JavascriptInterface
        public void onReceiveMessageData(final boolean willShowMessage, String _msgJSON) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flushOrSyncCookies();

                    if (willShowMessage) {
                        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.bringToFront();
                        webView.requestLayout();
                    } else {
                        ConsentLib.this.finish();
                    }
                }
            });
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onMessageChoiceSelect(int choiceType) throws ConsentLibException.NoInternetConnectionException {
            if(ConsentLib.this.hasLostInternetConnection()) {
                throw new ConsentLibException().new NoInternetConnectionException();
            }

            ConsentLib.this.choiceType = choiceType;
            ConsentLib.this.onMessageChoiceSelect.run(ConsentLib.this);
        }

        // called when interaction with message is complete
        @JavascriptInterface
        public void sendConsentData(final String euconsent, final String consentUUID) {
            SharedPreferences.Editor editor = sharedPref.edit();

            if (euconsent != null) {
                ConsentLib.this.euconsent = euconsent;
                editor.putString(EU_CONSENT_KEY, euconsent);
            }

            if (consentUUID != null) {
                ConsentLib.this.consentUUID = consentUUID;
                editor.putString(CONSENT_UUID_KEY, consentUUID);
            }

            if (euconsent != null || consentUUID != null) {
                editor.commit();
            }

            setIABVars(euconsent);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConsentLib.this.finish();
                }
            });
        }
    }

    /**
     * Simple encapsulating class for consents.
     */
    protected static abstract class Consent {
        public final String id;
        public final String name;
        public final String type;

        Consent(String id, String name, String type) {
            this.name = name;
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean equals(Object otherConsent) {
            return super.equals(((Consent) otherConsent).id);
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            try {
                json.put("id", id).put("name", name).put("type", type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public static class CustomPurposeConsent extends Consent {
        CustomPurposeConsent(String id, String name) {
            super(id, name, "CustomPurposeConsent");
        }
    }

    public static class CustomVendorConsent extends Consent {
        CustomVendorConsent(String id, String name) {
            super(id, name, "CustomVendorConsent");
        }
    }

    /**
     *
     * @return a new instance of ConsentLib.Builder
     */
    public static ActivityStep newBuilder() {
        return new Builder();
    }

    private ConsentLib(Builder b) throws ConsentLibException.ApiException {
        activity = b.activity;
        siteName = b.siteName;
        accountId = b.accountId;
        onMessageChoiceSelect = b.onMessageChoiceSelect;
        onInteractionComplete = b.onInteractionComplete;
        encodedTargetingParams = b.targetingParamsString;
        encodedDebugLevel = new EncodedParam("debugLevel", b.debugLevel.name());
        viewGroup = b.viewGroup;

        sourcePoint = new SourcePointClientBuilder(b.accountId, b.siteName+"/"+b.page, b.staging)
                .setStagingCampaign(b.stagingCampaign)
                .setCmpDomain(b.cmpDomain)
                .setMessageDomain(b.msgDomain)
                .setMmsDomain(b.mmsDomain)
                .build();

        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        euconsent = sharedPref.getString(EU_CONSENT_KEY, null);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);
    }

    private boolean hasLostInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) { return true; }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     * The Following keys should will be available in the shared preferences storage after this method
     * is called:
     * <ul>
     *     <li>{@link ConsentLib#IAB_CONSENT_CMP_PRESENT}</li>
     *     <li>{@link ConsentLib#IAB_CONSENT_SUBJECT_TO_GDPR}</li>
     * </ul>
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with ConsentLib
     */
    public void run() throws ConsentLibException.NoInternetConnectionException {
        if(hasLostInternetConnection()) {
            throw new ConsentLibException().new NoInternetConnectionException();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(activity);
        }

        cm = android.webkit.CookieManager.getInstance();
        final boolean acceptCookie = cm.acceptCookie();
        cm.setAcceptCookie(true);

        webView = new WebView(activity) {
            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                flushOrSyncCookies();
                cm.setAcceptCookie(acceptCookie);
            }
        };

        // allow third party cookies for loading mms and consent cookies
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.setAcceptThirdPartyCookies(webView, true);
        }

        // hide web view while it loads initially
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.addJavascriptInterface(new MessageInterface(), "JSReceiver");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(sourcePoint.messageUrl(encodedTargetingParams, encodedDebugLevel));
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                WebView webView = (WebView) view;
                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                    KeyEvent.KEYCODE_BACK == keyCode &&
                    webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        viewGroup.addView(webView);

        // Set standard IAB IABConsent_CMPPresent
        setSharedPreference(IAB_CONSENT_CMP_PRESENT, true);

        setSubjectToGDPR();
    }

    private void setSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSharedPreference(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void setSubjectToGDPR() {
        if (sharedPref.getString(IAB_CONSENT_SUBJECT_TO_GDPR, null) != null) { return; }

        sourcePoint.getGDPRStatus(new OnLoadComplete() {
            @Override
            public void onSuccess(Object gdprApplies) {
                setSharedPreference(IAB_CONSENT_SUBJECT_TO_GDPR, gdprApplies.equals("true") ? "1" : "0");
            }
        });
    }

    private void setIABVars(String euconsent) {
        setSharedPreference(IAB_CONSENT_CONSENT_STRING, euconsent);

        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(euconsent);

        // Construct and save parsed purposes string
        char[] allowedPurposes = new char[MAX_PURPOSE_ID];
        for (int i = 0;i < MAX_PURPOSE_ID; i++) {
            allowedPurposes[i] = vendorConsent.isPurposeAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG,"allowedPurposes: " + new String(allowedPurposes));
        setSharedPreference(IAB_CONSENT_PARSED_PURPOSE_CONSENTS, new String(allowedPurposes));

        // Construct and save parsed vendors string
        char[] allowedVendors = new char[vendorConsent.getMaxVendorId()];
        for (int i = 0;i < allowedVendors.length; i++) {
            allowedVendors[i] = vendorConsent.isVendorAllowed(i + 1) ? '1' : '0';
        }
        Log.i(TAG,"allowedVendors: " + new String(allowedVendors));
        setSharedPreference(IAB_CONSENT_PARSED_VENDOR_CONSENTS, new String(allowedVendors));
    }

    // get site id corresponding to account id and site name. Read from local storage if present.
    // Write to local storage after getting response.
    private void getSiteId(final OnLoadComplete callback) {
        final String siteIdKey = SP_SITE_ID + "_" + Integer.toString(accountId) + "_" + siteName;

        String storedSiteId = sharedPref.getString(siteIdKey, null);
        if (storedSiteId != null) {
            callback.onSuccess(storedSiteId);
            return;
        }

        sourcePoint.getSiteID(new OnLoadComplete() {
            @Override
            public void onSuccess(Object siteID) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(siteIdKey, (String) siteID);
                editor.commit();
                callback.onSuccess(siteID);
            }
        });
    }

    /**
     * This method receives an Array of Strings representing the custom vendor ids you want to get
     * the consents for and a callback.<br/>
     * The callback will be called with an Array of booleans once the data is ready. If the element
     * <i>i</i> of this array is <i>true</i> it means the user has consented to the vendor index <i>i</i>
     * from the customVendorIds parameter. Otherwise it will be <i>false</i>.
     * @param customVendorIds an array of vendor ids - currently needs to be provided by SourcePoint
     * @param callback - callback that will be called with an array of boolean indicating if the user has given consent or not to those vendors.
     * @throws ConsentLibException.ApiException will be throw in case something goes wrong when communicating with SourcePoint
     */
    public void getCustomVendorConsents(final String[] customVendorIds, final OnLoadComplete callback) throws ConsentLibException.ApiException {
        loadAndStoreCustomVendorAndPurposeConsents(customVendorIds, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                HashSet<Consent> consents = (HashSet<Consent>) result;
                HashSet<CustomVendorConsent> vendorConsents = new HashSet<>();
                for (Consent consent : consents) {
                    if(consent instanceof CustomVendorConsent) {
                        vendorConsents.add((CustomVendorConsent) consent);
                    }
                }
                callback.onSuccess(vendorConsents);
            }
        });
    }

    /**
     * This method receives a callback which is called with an Array of all the purposes ({@link Consent}) the user has given consent for.
     * @param callback called with an array of {@link Consent}
     * @throws ConsentLibException.ApiException will be throw in case something goes wrong when communicating with SourcePoint
     */
    public void getCustomPurposeConsents(final OnLoadComplete callback) throws ConsentLibException.ApiException {
        loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                HashSet<Consent> consents = (HashSet<Consent>) result;
                HashSet<CustomPurposeConsent> purposeConsents = new HashSet<>();
                for (Consent consent : consents) {
                    if(consent instanceof CustomPurposeConsent) {
                        purposeConsents.add((CustomPurposeConsent) consent);
                    }
                }
                callback.onSuccess(purposeConsents);
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
     *         consent string is not present in SharedPreferences.
     */
    public boolean[] getIABVendorConsents(int[] vendorIds) throws ConsentLibException{
        final VendorConsent vendorConsent = getParsedConsentString();
        boolean[] results = new boolean[vendorIds.length];

        for(int i = 0; i < vendorIds.length; i++) {
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
     *         consent string is not present in SharedPreferences.
     */
    public boolean[] getIABPurposeConsents(int[] purposeIds) throws ConsentLibException {
        final VendorConsent vendorConsent = getParsedConsentString();
        boolean[] results = new boolean[purposeIds.length];

        for(int i = 0; i < purposeIds.length; i++) {
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
        return VendorConsentDecoder.fromBase64String(euconsent);
    }

    /**
     * When we receive data from the server, if a given custom vendor is no longer given consent
     * to, its information won't be present in the payload. Therefore we have to first clear the
     * preferences then set each vendor to true based on the response.
     */
    private void clearStoredVendorConsents(final String[] customVendorIds, SharedPreferences.Editor editor) {
        for(String vendorId : customVendorIds){
            editor.remove(CUSTOM_CONSENTS_KEY + vendorId);
        }
    }

    private void loadAndStoreCustomVendorAndPurposeConsents(final String[] vendorIds, final OnLoadComplete callback) throws ConsentLibException.ApiException {
        getSiteId(new OnLoadComplete() {
            @Override
            public void onSuccess(Object siteId) {
                sourcePoint.getCustomConsents(consentUUID, euconsent, (String) siteId, vendorIds, new OnLoadComplete() {
                    @Override
                    public void onSuccess(Object result) {
                        HashSet<Consent> consents = (HashSet<Consent>) result;
                        HashSet<String> consentStrings = new HashSet<>();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        clearStoredVendorConsents(vendorIds, editor);
                        for(Consent consent : consents) {
                            consentStrings.add(consent.toJSON().toString());
                        }
                        editor.putStringSet(CUSTOM_CONSENTS_KEY, consentStrings);
                        editor.commit();
                        callback.onSuccess(consents);
                    }
                });

            }
        });
    }

    private void finish() {
        onInteractionComplete.run(this);
        viewGroup.removeView(webView);
        this.activity = null; // release reference to activity
    }
}

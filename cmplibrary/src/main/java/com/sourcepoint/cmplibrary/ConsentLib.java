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
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.consent.VendorConsentDecoder;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents as well as IAB consent purposes.
 * <pre>{@code
 *      ConsentLib cLib = ConsentLib.newBuilder()
 *                     .setActivity(this)
 *                     .setAccountId(YOUR_ACCOUNT_ID)
 *                     .setSiteName("A_SITE_NAME")
 *                     .setOnInteractionComplete(new ConsentLib.Callback() {
 *                         public void run(ConsentLib c) {
 *                                 c.getCustomVendorConsents(
 *                                         new String[]{"VENDOR_ID"},
 *                                         new ConsentLib.OnLoadComplete() {
 *                                             public void onSuccess(Object result) {
 *                                                 Log.i(TAG, "custom vendor consent 1: " + ((boolean[]) result)[0]);
 *                                             }
 *                                         });
 *                                 c.getPurposeConsents(
 *                                         new ConsentLib.OnLoadComplete() {
 *                                             public void onSuccess(Object result) {
 *                                                 ConsentLib.PurposeConsent[] results = (ConsentLib.PurposeConsent[]) result;
 *                                                 for (ConsentLib.PurposeConsent purpose : results) {
 *                                                     Log.i(TAG, "Consented to purpose: " + purpose.name);
 *                                                 }
 *                                             }
 *                                         });
 *                                 c.getPurposeConsent(
 *                                         "PURPOSE_ID",
 *                                         new ConsentLib.OnLoadComplete() {
 *                                             public void onSuccess(Object result) {
 *                                                 Log.i(TAG, "Consented to PURPOSE: " + ((Boolean) result).toString());
 *                                             }
 *                                         });
 *                         }
 *                     })
 *                     .build();
 *                 cLib.run();
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

    private static final String DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.cmp.sp-stage.net/";
    private static final String DEFAULT_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.sourcepoint.mgr.consensu.org/";
    private static final String DEFAULT_INTERNAL_MMS_DOMAIN = "mms.sp-stage.net";
    private static final String DEFAULT_MMS_DOMAIN = "mms.sp-prod.net";
    private static final String DEFAULT_INTERNAL_CMP_DOMAIN = "cmp.sp-stage.net";
    private static final String DEFAULT_CMP_DOMAIN = "sourcepoint.mgr.consensu.org";

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

    private final static String CUSTOM_VENDOR_PREFIX = SP_PREFIX + "_custom_vendor_consent_";
    private final static String CUSTOM_PURPOSE_PREFIX = SP_PREFIX + "_custom_purpose_consent_";
    private final static String CUSTOM_PURPOSE_CONSENTS_JSON = SP_PREFIX + "_custom_purpose_consents_json";

    private Activity activity;
    private final String siteName;
    private final int accountId;
    private final ViewGroup viewGroup;
    private final Callback onMessageChoiceSelect;
    private final Callback onInteractionComplete;
    private final boolean isStage;
    private final String inAppMessagingPageUrl;
    private final String mmsDomain;
    private final EncodedAttribute encodedMsgDomain;
    private final String cmpDomain;
    private final EncodedAttribute encodedCmpOrigin;
    private final EncodedAttribute encodedTargetingParams;
    private final DebugLevel debugLevel;
    private final EncodedAttribute encodedHref;

    private final SourcePointClient sourcePoint;

    private final SharedPreferences sharedPref;

    private android.webkit.CookieManager cm;

    private WebView webView;

    private static class EncodedAttribute {
        private String value;

        EncodedAttribute(String name, String value) throws ConsentLibException {
            this.value = encode(name, value);
        }

        private String encode(String attrName, String attrValue) throws ConsentLibException {
            try {
                return URLEncoder.encode(attrValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ConsentLibException("Unable to encode "+attrName+", with the value: "+attrValue);
            }
        }

        String getValue() { return value; }

        @Override
        public String toString() { return getValue(); }
    }

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
         *  {@link ConsentLib#getPurposeConsents} and {@link ConsentLib#getPurposeConsent}
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
        private boolean isStage = false;
        private boolean isInternalStage = false;
        private String inAppMessagingPageUrl = null;
        private String mmsDomain = null;
        private String cmpDomain = null;
        private EncodedAttribute msgDomain = null;
        private EncodedAttribute cmpOrign = null;
        private EncodedAttribute href = null;
        private final JSONObject targetingParams = new JSONObject();
        private EncodedAttribute targetingParamsString = null;
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
         *  {@link ConsentLib#getPurposeConsents} and {@link ConsentLib#getPurposeConsent}
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
            isStage = st;
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
            isInternalStage = st;
            return this;
        }

        public BuildStep setInAppMessagePageUrl(String pageUrl) {
            inAppMessagingPageUrl = pageUrl;
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

        private void setCmpOrign() throws ConsentLibException {
            cmpOrign = new EncodedAttribute("cmpOrigin", "//" + cmpDomain);
        }

        private void setMsgDomain() throws ConsentLibException {
             msgDomain = new EncodedAttribute("mmsDomain", "//" + mmsDomain);
        }

        private void setTargetingParamsString() throws ConsentLibException {
            targetingParamsString = new EncodedAttribute("targetingParams", targetingParams.toString());
        }

        private void setHref() throws ConsentLibException {
            href = new EncodedAttribute("href", "http://" + siteName + "/" + page);
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
            if (inAppMessagingPageUrl == null) {
                inAppMessagingPageUrl = isInternalStage ?
                        DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_URL :
                        DEFAULT_IN_APP_MESSAGING_PAGE_URL;
            }

            if (mmsDomain == null) {
                mmsDomain = isInternalStage ? DEFAULT_INTERNAL_MMS_DOMAIN : DEFAULT_MMS_DOMAIN;
            }

            if (cmpDomain == null) {
                cmpDomain = isInternalStage ? DEFAULT_INTERNAL_CMP_DOMAIN : DEFAULT_CMP_DOMAIN;
            }

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
                setCmpOrign();
                setMsgDomain();
                setTargetingParamsString();
                setHref();
                validate();
            } catch (ConsentLibException e) {
                this.activity = null; // release reference to activity
                throw new ConsentLibException().new BuildException(e.getMessage());
            }

            return new ConsentLib(this);
        }
    }

    static class LoadTask extends AsyncTask<String, Void, Object> {
        private final OnLoadComplete listener;
        private ConsentLibException.ApiException apiException;

        LoadTask(OnLoadComplete listener) {
            this.listener = listener;
        }

        protected Object doInBackground(String... urlString) {
            Object result = new Object();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urlString[0]);
                urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int inputData = inputStream.read();
                while (inputData != -1) {
                    outputStream.write(inputData);
                    inputData = inputStream.read();
                }
                result = outputStream.toString();
            } catch (IOException e) {
                cancel(true);
                apiException = new ConsentLibException().new ApiException(e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        protected void onPostExecute(Object result) {
            listener.onSuccess(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            listener.onFailure(apiException);
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
     * Simple encapsulating class for purpose consents. Contains Purpose Id and Name as attributes;
     */
    public class PurposeConsent {
        @SuppressWarnings("unused")
        public final String id;
        public final String name;

        PurposeConsent(String id, String name) {
            this.name = name;
            this.id = id;
        }
    }

    /**
     *
     * @return a new instance of ConsentLib.Builder
     */
    public static ActivityStep newBuilder() {
        return new Builder();
    }

    private void load(String urlString, OnLoadComplete callback) {
        new LoadTask(callback).execute(urlString);
    }

    private ConsentLib(Builder b) throws ConsentLibException.ApiException {
        activity = b.activity;
        siteName = b.siteName;
        accountId = b.accountId;
        onMessageChoiceSelect = b.onMessageChoiceSelect;
        onInteractionComplete = b.onInteractionComplete;
        isStage = b.isStage;
        encodedTargetingParams = b.targetingParamsString;
        debugLevel = b.debugLevel;
        inAppMessagingPageUrl = b.inAppMessagingPageUrl;
        mmsDomain = b.mmsDomain;
        cmpDomain = b.cmpDomain;
        viewGroup = b.viewGroup;
        encodedCmpOrigin = b.cmpOrign;
        encodedMsgDomain = b.msgDomain;
        encodedHref = b.href;


        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        euconsent = sharedPref.getString(EU_CONSENT_KEY, null);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);

        sourcePoint = new SourcePointClient(Integer.toString(accountId), "https://"+siteName, false);
    }

    private String getSiteIdUrl() {
        return "http://" + mmsDomain + "/get_site_data?account_id=" + Integer.toString(accountId) + "&href=" + encodedHref;
    }

    private String getGDPRUrl() {
        return "https://" + cmpDomain + "/consent/v2/gdpr-status";
    }

    private String inAppMessageRequest() {
        List<String> params = new ArrayList<>();
        params.add("_sp_accountId=" + String.valueOf(accountId));
        params.add("_sp_cmp_inApp=true");
        params.add("_sp_writeFirstPartyCookies=true");
        params.add("_sp_siteHref=" + encodedHref);
        params.add("_sp_msg_domain=" + encodedMsgDomain);
        params.add("_sp_cmp_origin=" + encodedCmpOrigin);
        params.add("_sp_msg_targetingParams=" + encodedTargetingParams);
        params.add("_sp_debug_level=" + debugLevel.name());
        params.add("_sp_msg_stageCampaign=" + isStage);

        String url = inAppMessagingPageUrl + "?" + TextUtils.join("&", params);
        Log.i(TAG, "cpm url: " + url);
        return url;
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
        webView.loadUrl(inAppMessageRequest());
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {
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

        load(getGDPRUrl(), new OnLoadComplete() {
            @Override
            public void onLoadCompleted(Object result) throws ConsentLibException.ApiException {
                try {
                    String gdprApplies = new JSONObject((String) result).getString("gdprApplies");
                    setSharedPreference(IAB_CONSENT_SUBJECT_TO_GDPR, gdprApplies.equals("true") ? "1" : "0");
                } catch (JSONException e) {
                    throw new ConsentLibException().new ApiException("Failed to get GDPR status. Response from CMP Domain ("+cmpDomain+"):"+result);
                }
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
    private void getSiteId(final OnLoadComplete callback) throws ConsentLibException.ApiException {
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
     * This method receives a String indicating the custom vendor id and a callback that will be called
     * with <i>true</i> or <i>false</i> indicating if the user has given consent or not to that vendor.
     * @param customVendorId custom vendor id - currently needs to be provided by SourcePoint
     * @param callback - callback that will be called with a boolean indicating if the user has given consent or not to that vendor.
     * @throws ConsentLibException.ApiException will be throw in case something goes wrong when communicating with SourcePoint
     */
    @SuppressWarnings("unused")
    public void getCustomVendorConsent(final String customVendorId, final OnLoadComplete callback) throws ConsentLibException.ApiException {
        getCustomVendorConsents(new String[]{customVendorId}, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                callback.onSuccess(((ArrayList) result).get(0));
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
            public void onSuccess(Object _result) {
                ArrayList<Boolean> consents = new ArrayList<>();
                for (String vendorId : customVendorIds) {
                    String storedConsent = sharedPref.getString(CUSTOM_VENDOR_PREFIX + vendorId, "");
                    consents.add(storedConsent.equals("true"));
                }
                callback.onSuccess(consents);
            }
        });
    }

    /**
     * This method receives a String indicating the id of a purpose and a callback that will be called
     * with <i>true</i> or <i>false</i> indicating if the user has given consent or not to that purpose.
     * @param id the id of a purpose - needs to be provided by SourcePoint
     * @param callback - called with a boolean indicating if the user has given consent to that purpose
     * @throws ConsentLibException.ApiException will be throw in case something goes wrong when communicating with SourcePoint
     * @see ConsentLib#getPurposeConsents(OnLoadComplete)
     */
    public void getPurposeConsent(final String id, final OnLoadComplete callback) throws ConsentLibException.ApiException {
        getPurposeConsents(new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                boolean consented = false;
                PurposeConsent[] consents = (PurposeConsent[]) result;
                for(PurposeConsent consent : consents) {
                    if(consent.id.equals(id)) {
                        consented = true;
                    }
                }
                callback.onSuccess(consented);
            }
        });
    }

    /**
     * This method receives a callback which is called with an Array of all the purposes ({@link PurposeConsent}) the user has given consent for.
     * @param callback called with an array of {@link PurposeConsent}
     * @throws ConsentLibException.ApiException will be throw in case something goes wrong when communicating with SourcePoint
     */
    public void getPurposeConsents(final OnLoadComplete callback) throws ConsentLibException.ApiException {
        loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
                @Override
                public void onSuccess(Object result) {
                callback.onSuccess(
                        parsePurposeConsentJson(sharedPref.getString(CUSTOM_PURPOSE_CONSENTS_JSON, "[]"))
                );
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
    public boolean[] getIABPurposeConsents(int[] purposeIds) throws ConsentLibException{
        final VendorConsent vendorConsent = getParsedConsentString();
        boolean[] results = new boolean[purposeIds.length];

        for(int i = 0; i < purposeIds.length; i++) {
            results[i] = vendorConsent.isPurposeAllowed(purposeIds[i]);
        }
        return results;
    }

    private String getConsentStringFromPreferences() throws ConsentLibException{
        final String euconsent = sharedPref.getString(IAB_CONSENT_CONSENT_STRING, null);
        if (euconsent == null) {
            throw new ConsentLibException("Could not find consent string in sharedUserPreferences.");
        }
        return euconsent;
    }

    private VendorConsent getParsedConsentString() throws ConsentLibException{
        final String euconsent = getConsentStringFromPreferences();
        return VendorConsentDecoder.fromBase64String(euconsent);
    }

    private PurposeConsent[] parsePurposeConsentJson(String json) {

        try {
            JSONArray array = new JSONArray(json);
            PurposeConsent[] results = new PurposeConsent[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject consentJson = array.getJSONObject(i);
                results[i] = new PurposeConsent(
                        consentJson.getString("_id"),
                        consentJson.getString("name")
                );
            }
            return results;
        } catch (JSONException e) {
            return new PurposeConsent[] {};
//            throw new ConsentLibException().new ApiException("Could not extract purpose consent '_id' and 'name' from: "+json);
        }
    }

    private String getConsentRequest(String siteId, String[] vendorIds) {
        String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
        String euconsentParam = euconsent == null ? "[EUCONSENT]" : euconsent;
        String customVendorIdString = URLEncoder.encode(TextUtils.join(",", vendorIds));

        return "https://" + cmpDomain + "/consent/v2/" + siteId + "/custom-vendors?"+
                "customVendorIds=" + customVendorIdString +
                "&consentUUID=" + consentParam +
                "&euconsent=" + euconsentParam;
    }

    /**
     * When we receive data from the server, if a given custom vendor is no longer given consent
     * to, its information won't be present in the payload. Therefore we have to first clear the
     * preferences then set each vendor to true based on the response.
     */
    private void clearStoredVendorConsents(final String[] customVendorIds, SharedPreferences.Editor editor) {
        for(String vendorId : customVendorIds){
            editor.remove(CUSTOM_VENDOR_PREFIX + vendorId);
        }
    }

    private void loadAndStoreCustomVendorAndPurposeConsents(final String[] customVendorIdsToRequest, final OnLoadComplete callback) throws ConsentLibException.ApiException {
        getSiteId(new OnLoadComplete() {
            @Override
            public void onSuccess(Object siteId) {
                final String consentUrl = getConsentRequest(siteId.toString(), customVendorIdsToRequest);

                load(consentUrl, new OnLoadComplete() {
                    @Override
                    public void onSuccess(Object vendorsAndPurposes) {
                        if(vendorsAndPurposes instanceof FileNotFoundException) {
//                            throw new ConsentLibException()
//                                    .new ApiException("404: could not find vendor consents and purposes from: "+consentUrl);
                        }


                        JSONArray consentedCustomVendors;
                        JSONArray consentedCustomPurposes;
                        try {
                            JSONObject consentedCustomData = new JSONObject(vendorsAndPurposes.toString());
                            consentedCustomVendors = consentedCustomData.getJSONArray("consentedVendors");
                            consentedCustomPurposes = consentedCustomData.getJSONArray("consentedPurposes");
                        } catch (JSONException e) {
                            throw new ConsentLibException()
                                    .new ApiException("Error extracting consented vendors and purposes from server's response: "+vendorsAndPurposes);
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        clearStoredVendorConsents(customVendorIdsToRequest, editor);
                        for (int i = 0; i < consentedCustomVendors.length(); i++) {
                            try {
                                JSONObject consentedCustomVendor = consentedCustomVendors.getJSONObject(i);
                                editor.putString(CUSTOM_VENDOR_PREFIX + consentedCustomVendor.get("_id"), Boolean.toString(true));
                            } catch (JSONException e) {
                                throw new ConsentLibException().new ApiException("Could not extract customVendors from: "+vendorsAndPurposes);
                            }
                        }

                        editor.putString(CUSTOM_PURPOSE_CONSENTS_JSON, consentedCustomPurposes.toString());

                        editor.commit();
                        callback.onLoadCompleted(vendorsAndPurposes.toString());
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

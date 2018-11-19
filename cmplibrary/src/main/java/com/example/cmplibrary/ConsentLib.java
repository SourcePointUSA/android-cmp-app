package com.example.cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.consent.VendorConsentDecoder;

/**
 * Created by dmitrirabinowitz on 8/15/18.
 */

public class ConsentLib {
    ////
    //// Public
    ////
    public static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";
    public static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";
    public static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";
    public static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";
    public static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";

    private static final String DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.cmp.sp-stage.net/";
    private static final String DEFAULT_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.sourcepoint.mgr.consensu.org/";
    private static final String DEFAULT_INTERNAL_MMS_DOMAIN = "mms.sp-stage.net";
    private static final String DEFAULT_MMS_DOMAIN = "mms.sp-prod.net";
    private static final String DEFAULT_INTERNAL_CMP_DOMAIN = "cmp.sp-stage.net";
    private static final String DEFAULT_CMP_DOMAIN = "sourcepoint.mgr.consensu.org";

    public enum DebugLevel {
        DEBUG,
        INFO,
        TIME,
        WARN,
        ERROR,
        OFF
    }
    // visible for grabbing consent from shared preferences
    public static final String EU_CONSENT_KEY = "euconsent";
    public static final String CONSENT_UUID_KEY = "consentUUID";
    public static final int MAX_PURPOSE_ID = 24;


    public String msgJSON = null;
    public Integer choiceType = null;
    public String euconsent = null;
    public JSONObject[] customConsent = null;
    public String consentUUID = null;

    ////
    //// Private
    ////
    private static final String TAG = "ConsentLib";

    private static final String SP_PREFIX = "_sp_";
    private static final String SP_SITE_ID = SP_PREFIX + "site_id";

    private final static String CUSTOM_VENDOR_PREFIX = SP_PREFIX + "_custom_vendor_consent_";
    private final static String CUSTOM_PURPOSE_PREFIX = SP_PREFIX + "_custom_purpose_consent_";
    private final static String CUSTOM_PURPOSE_CONSENTS_JSON = SP_PREFIX + "_custom_purpose_consents_json";

    private final Activity activity;
    private final String siteName;
    private final int accountId;
    private final ViewGroup viewGroup;
    private final Callback onReceiveMessageData;
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

    private final SharedPreferences sharedPref;

    private android.webkit.CookieManager cm;

    private WebView webView;

    private static class EncodedAttribute {
        private String value;

        public EncodedAttribute(String name, String value) throws ConsentLibException {
            this.value = encode(name, value);
        }

        private String encode(String attrName, String attrValue) throws ConsentLibException {
            try {
                return URLEncoder.encode(attrValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ConsentLibException("Unable to encode "+attrName+", with the value: "+attrValue);
            }
        }

        public String getValue() { return value; }

        @Override
        public String toString() { return getValue(); }
    }

    ////
    //// Interfaces
    ////
    public interface Callback {
        void run(ConsentLib c);
    }

    /*
     * Use step pattern to enforce ConsentLib is built with required parameters
     * https://www.javacodegeeks.com/2013/05/building-smart-builders.html
     */
    public interface ActivityStep {
        AccountIdStep setActivity(Activity a);
    }

    public interface AccountIdStep {
        SiteNameStep setAccountId(int id);
    }

    public interface SiteNameStep {
        BuildStep setSiteName(String s);
    }

    public interface OnLoadComplete {
        void onLoadCompleted(Object result);
    }

    /*
     * Set all optional parameters here and build
     */
    public interface BuildStep {
        BuildStep setPage(String s);

        BuildStep setViewGroup(ViewGroup v);

        BuildStep setOnReceiveMessageData(Callback c);

        BuildStep setOnMessageChoiceSelect(Callback c);

        BuildStep setOnInteractionComplete(Callback c);

        BuildStep setStage(boolean st);

        BuildStep setInternalStage(boolean st);

        BuildStep setInAppMessagePageUrl(String pageUrl);

        BuildStep setMmsDomain(String mmsDomain);

        BuildStep setCmpDomain(String cmpDomain);

        BuildStep setTargetingParam(String key, Integer val) throws ConsentLibException.BuildException ;

        BuildStep setTargetingParam(String key, String val) throws ConsentLibException.BuildException ;

        BuildStep setDebugLevel(DebugLevel l);

        ConsentLib build() throws ConsentLibException.BuildException;
    }


    ////
    //// Inner classes
    ////

    private static class Builder implements ActivityStep, AccountIdStep, SiteNameStep, BuildStep {
        private Activity activity;
        private int accountId;
        private String siteName;
        private String page = "";
        private ViewGroup viewGroup = null;
        private Callback noOpCallback = new Callback() { @Override public void run(ConsentLib c) { } };
        private Callback onReceiveMessageData = noOpCallback;
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
        private JSONObject targetingParams = new JSONObject();
        private EncodedAttribute targetingParamsString = null;
        private DebugLevel debugLevel = DebugLevel.OFF;

        public AccountIdStep setActivity(Activity a) {
            activity = a;
            return this;
        }

        public SiteNameStep setAccountId(int id) {
            accountId = id;
            return this;
        }

        public BuildStep setSiteName(String s) {
            siteName = s;
            return this;
        }

        public BuildStep setPage(String p) {
            page = p;
            return this;
        }

        public BuildStep setViewGroup(ViewGroup v) {
            viewGroup = v;
            return this;
        }

        public BuildStep setOnReceiveMessageData(Callback c) {
            onReceiveMessageData = c;
            return this;
        }

        public BuildStep setOnMessageChoiceSelect(Callback c) {
            onMessageChoiceSelect = c;
            return this;
        }

        public BuildStep setOnInteractionComplete(Callback c) {
            onInteractionComplete = c;
            return this;
        }

        public BuildStep setStage(boolean st) {
            isStage = st;
            return this;
        }

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

        public ConsentLib build() throws ConsentLibException.BuildException {
            try {
                setDefaults();
                setCmpOrign();
                setMsgDomain();
                setTargetingParamsString();
                setHref();
                validate();
            } catch (ConsentLibException e) {
                throw new ConsentLibException().new BuildException(e.getMessage());
            }

            return new ConsentLib(this);
        }
    }


    class LoadTask extends AsyncTask<String, Void, String> {
        private OnLoadComplete listener;
        private String urlToLoad;

        public LoadTask(OnLoadComplete listener) {
            this.listener = listener;
        }

        protected String doInBackground(String... urlString) {
            URL url;
            this.urlToLoad = urlString[0];
            try {
                url = new URL(urlString[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                try {
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = in.read();
                    while (i != -1) {
                        bo.write(i);
                        i = in.read();
                    }
                    return bo.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                urlConnection.disconnect();
            }
        }

        protected void onPostExecute(String result) {
            //Log.i(TAG, "Successfully loaded " + this.urlToLoad + ". result: " + result);
            listener.onLoadCompleted(result);
        }
    }

    private class MessageInterface {

        // called when message loads
        @JavascriptInterface
        public void onReceiveMessageData(final boolean willShowMessage, String msgJSON) {
            ConsentLib.this.msgJSON = msgJSON;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cm.getInstance().flush();
                    }
                    {
                        android.webkit.CookieSyncManager.getInstance().sync();
                    }

                    ConsentLib.this.onReceiveMessageData.run(ConsentLib.this);

                    // show web view once we confirm the message is ready to display
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
            if(!ConsentLib.this.isThereInternetConnection()) {
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


    public class PurposeConsent {
        public String name;
        public String id;
        public PurposeConsent(String id, String name) {
            this.name = name;
            this.id = id;
        }
    }

    ////
    //// Static functons
    ////

    public static ActivityStep newBuilder() {
        return new Builder();
    }

    ////
    //// Member Functions
    ////

    private void load(String urlString, OnLoadComplete callback) {
        new LoadTask(callback).execute(urlString);
    }

    private ConsentLib(Builder b) {
        activity = b.activity;
        siteName = b.siteName;
        accountId = b.accountId;
        onReceiveMessageData = b.onReceiveMessageData;
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
    }

    private String getSiteIdUrl() {
        return "http://" + mmsDomain + "/get_site_data?account_id=" + Integer.toString(accountId) + "&href=" + encodedHref;
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

        return inAppMessagingPageUrl + "?" + TextUtils.join("&", params);
    }

    private boolean isThereInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) { return false; }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void run() throws ConsentLibException.NoInternetConnectionException {
        if(!isThereInternetConnection()) {
            throw new ConsentLibException().new NoInternetConnectionException();
        }

        cm = android.webkit.CookieManager.getInstance();
        final boolean acceptCookie = cm.acceptCookie();
        cm.setAcceptCookie(true);

        webView = new WebView(activity) {
            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();

                // sync cookies on detach window so we don't miss cookie updates when the app is
                // closed with a message open
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cm.getInstance().flush();
                }
                {
                    android.webkit.CookieSyncManager.getInstance().sync();
                }

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

        viewGroup.addView(webView);

        // Set standard IAB IABConsent_CMPPresent
        setSharedPreference(IAB_CONSENT_CMP_PRESENT, "1");

        setSubjectToGDPR();
    }

    private void setSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSubjectToGDPR() {
        String currentVal = sharedPref.getString(IAB_CONSENT_SUBJECT_TO_GDPR, null);
        if (currentVal != null) {
            return;
        }
        String url = "https://" + cmpDomain + "/consent/v2/gdpr-status";

        load(url, new OnLoadComplete() {
            @Override
            public void onLoadCompleted(Object result) {
                try {
                    String gdprApplies= new JSONObject((String) result).getString("gdprApplies");
                    setSharedPreference(IAB_CONSENT_SUBJECT_TO_GDPR, gdprApplies.equals("true") ? "1" : "0");
                } catch (JSONException e) {
                    e.printStackTrace();
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
    private void getSiteId(final OnLoadComplete callback) {
        final String siteIdKey = SP_SITE_ID + "_" + Integer.toString(accountId) + "_" + siteName;

        String storedSiteId = sharedPref.getString(siteIdKey, null);
        if (storedSiteId != null) {
            callback.onLoadCompleted(storedSiteId);
            return;
        }

        load(
                getSiteIdUrl(),
                new OnLoadComplete() {
                    @Override
                    public void onLoadCompleted(Object result) {
                        String siteId;
                        try {
                            siteId = new JSONObject((String) result).getString("site_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onLoadCompleted(null);
                            return;
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(siteIdKey, siteId);
                        editor.commit();
                        callback.onLoadCompleted(siteId);
                    }
                }
        );
    }

    public void getCustomVendorConsent(final String customVendorId, final OnLoadComplete callback) {
        getCustomVendorConsents(new String[]{customVendorId}, new OnLoadComplete() {
            @Override
            public void onLoadCompleted(Object result) {
                callback.onLoadCompleted(((String[]) result)[0]);
            }
        });
    }

    public void getCustomVendorConsents(final String[] customVendorIds, final OnLoadComplete callback) {
        boolean[] storedResults = new boolean[customVendorIds.length];
        // read results from local storage if present first
        List<String> customVendorIdsToRequest = new ArrayList<>();
        for (int i = 0; i < customVendorIds.length; i++) {
            String customVendorId = customVendorIds[i];
            String storedConsentData = sharedPref.getString(CUSTOM_VENDOR_PREFIX + customVendorId, null);
            if (storedConsentData == null) {
                customVendorIdsToRequest.add(customVendorId);
            } else {
                storedResults[i] = Boolean.getBoolean(storedConsentData);
            }
        }
        if (customVendorIdsToRequest.size() == 0) {
            callback.onLoadCompleted(storedResults);
            return;
        }

        final boolean[] finalStoredResults = storedResults;

        loadAndStoreCustomVendorAndPurposeConsents(customVendorIdsToRequest.toArray(new String[0]), new OnLoadComplete() {
            @Override
            public void onLoadCompleted(Object _result) {
                boolean[] results = finalStoredResults;
                for (int i = 0; i < customVendorIds.length; i++) {
                    String customVendorId = customVendorIds[i];
                    String storedConsentData = sharedPref.getString(CUSTOM_VENDOR_PREFIX + customVendorId, null);
                    if (storedConsentData != null) {
                        results[i] = storedConsentData.equals("true");
                    }
                }
                callback.onLoadCompleted(results);
            }
        });

    }

    public void getPurposeConsent(final String id, final OnLoadComplete callback) {
        final String storedPurposeConsent = sharedPref.getString(CUSTOM_PURPOSE_PREFIX + id, null);
        if (storedPurposeConsent != null) {
            callback.onLoadCompleted(storedPurposeConsent.equals("true"));
        } else {
            loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
                @Override
                public void onLoadCompleted(Object result) {
                    String storedPurposeConsent = sharedPref.getString(CUSTOM_PURPOSE_PREFIX + id, null);
                    callback.onLoadCompleted(
                            storedPurposeConsent!= null && storedPurposeConsent.equals("true")
                    );
                }
            });
        }
    }

    public void getPurposeConsents(final OnLoadComplete callback) {
        String storedPurposeConsentsJson = sharedPref.getString(CUSTOM_PURPOSE_CONSENTS_JSON, null);
        if (storedPurposeConsentsJson != null) {
            callback.onLoadCompleted(parsePurposeConsentJson(storedPurposeConsentsJson));
        } else {
            loadAndStoreCustomVendorAndPurposeConsents(new String[0], new OnLoadComplete() {
                @Override
                public void onLoadCompleted(Object result) {
                    callback.onLoadCompleted(
                            parsePurposeConsentJson(
                                    sharedPref.getString(CUSTOM_PURPOSE_CONSENTS_JSON, null)
                            )
                    );
                }
            });
        }
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
            // Should handle error here
            e.printStackTrace();
            return null;
        }
    }

    private void loadAndStoreCustomVendorAndPurposeConsents(final String[] customVendorIdsToRequest, final OnLoadComplete callback) {
        getSiteId(new OnLoadComplete() {

            @Override
            public void onLoadCompleted(Object result) {
                if (result == null) {
                    callback.onLoadCompleted(null);
                    return;
                }
                String siteId = (String) result;

                String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
                String euconsentParam = euconsent == null ? "[EUCONSENT]" : euconsent;
                String customVendorIdString = URLEncoder.encode(TextUtils.join(",", customVendorIdsToRequest));
                String url = "https://" + cmpDomain + "/consent/v2/" + siteId +
                        "/custom-vendors?customVendorIds=" + customVendorIdString +
                        "&consentUUID=" + consentParam +
                        "&euconsent=" + euconsentParam;
                load(url, new OnLoadComplete() {

                    @Override
                    public void onLoadCompleted(Object result) {
                        String response = (String) result;

                        JSONArray consentedCustomVendors;
                        JSONArray consentedCustomPurposes;

                        try {
                            JSONObject consentedCustomData = new JSONObject(response);
                            consentedCustomVendors = consentedCustomData.getJSONArray("consentedVendors");
                            consentedCustomPurposes = consentedCustomData.getJSONArray("consentedPurposes");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onLoadCompleted(null);
                            return;
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        // write results to local storage after reading from endpoint
                        for (int i = 0; i < consentedCustomVendors.length(); i++) {
                            try {
                                JSONObject consentedCustomVendor = consentedCustomVendors.getJSONObject(i);
                                editor.putString(CUSTOM_VENDOR_PREFIX + consentedCustomVendor.get("_id"), Boolean.toString(true));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        editor.putString(CUSTOM_PURPOSE_CONSENTS_JSON, consentedCustomPurposes.toString());
                        for (int i = 0; i < consentedCustomPurposes.length(); i++) {
                            try {
                                JSONObject consentedCustomPurpose = consentedCustomPurposes.getJSONObject(i);
                                editor.putString(
                                        CUSTOM_PURPOSE_PREFIX + consentedCustomPurpose.get("_id"), Boolean.toString(true));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        editor.commit();
                        callback.onLoadCompleted(result);
                    }
                });
            }
        });
    }

    private void finish() {
        onInteractionComplete.run(this);
        viewGroup.removeView(webView);
    }
}

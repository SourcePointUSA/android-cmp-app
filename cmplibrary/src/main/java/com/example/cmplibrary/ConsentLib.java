package com.example.cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrirabinowitz on 8/15/18.
 */

public class ConsentLib {

    public enum DebugLevel {
        DEBUG,
        INFO,
        TIME,
        WARN,
        ERROR,
        OFF;
    }

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
        BuildStep setTargetingParam(String key, Integer val);
        BuildStep setTargetingParam(String key, String val);
        BuildStep setDebugLevel(DebugLevel l);
        ConsentLib build();
    }

    public static ActivityStep newBuilder() {
        return new Builder();
    }

    private static class Builder implements ActivityStep, AccountIdStep, SiteNameStep, BuildStep {
        private Activity activity;
        private int accountId;
        private String siteName;
        private String page = "";
        private ViewGroup viewGroup = null;
        private Callback onReceiveMessageData = null;
        private Callback onMessageChoiceSelect = null;
        private Callback onInteractionComplete = null;
        private boolean isStage = false;
        private boolean isInternalStage = false;
        private String inAppMessagingPageUrl = null;
        private String mmsDomain = null;
        private String cmpDomain = null;
        private JSONObject targetingParams = new JSONObject();
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
        public BuildStep setTargetingParam(String key, Integer val) {
            return setTargetingParam(key, (Object) val);
        }
        public BuildStep setTargetingParam(String key, String val) {
            return setTargetingParam(key, (Object) val);
        }
        private BuildStep setTargetingParam(String key, Object val) {
            try {
                this.targetingParams.put(key, val);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public BuildStep setDebugLevel(DebugLevel l) {
            debugLevel = l;
            return this;
        }
        public ConsentLib build() {
            return new ConsentLib(this);
        }
    }

    private static final String TAG = "ConsentLib";

    // visible for grabbing consent from shared preferences
    public static final String EU_CONSENT_KEY = "euconsent";
    public static final String CONSENT_UUID_KEY = "consentUUID";

    private static final String SP_PREFIX = "_sp_";
    private static final String SP_SITE_ID = SP_PREFIX + "site_id";

    private final Activity activity;
    private final String siteName;
    private final int accountId;
    private final String page;
    private final ViewGroup viewGroup;
    private final Callback onReceiveMessageData;
    private final Callback onMessageChoiceSelect;
    private final Callback onInteractionComplete;
    private final boolean isStage;
    private final boolean isInternalStage;
    private final String inAppMessagingPageUrl;
    private final String mmsDomain;
    private final String cmpDomain;
    private final JSONObject targetingParams;
    private final DebugLevel debugLevel;

    private final SharedPreferences sharedPref;

    private android.webkit.CookieManager cm;

    private WebView webView;

    public String msgJSON = null;
    public Integer choiceType = null;
    public String euconsent = null;
    public JSONObject[] customConsent = null;
    public String consentUUID = null;

    private static String load(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
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

    private ConsentLib(Builder b) {
        activity = b.activity;
        siteName = b.siteName;
        accountId = b.accountId;
        page = b.page;
        onReceiveMessageData = b.onReceiveMessageData;
        onMessageChoiceSelect = b.onMessageChoiceSelect;
        onInteractionComplete = b.onInteractionComplete;
        isStage = b.isStage;
        isInternalStage = b.isInternalStage;
        targetingParams = b.targetingParams;
        debugLevel = b.debugLevel;

        if (b.inAppMessagingPageUrl == null) {
            inAppMessagingPageUrl = isInternalStage ? "http://in-app-messaging.pm.cmp.sp-stage.net/" : "http://in-app-messaging.pm.sourcepoint.mgr.consensu.org/";
        } else {
            inAppMessagingPageUrl = b.inAppMessagingPageUrl;
        }

        if (b.mmsDomain == null) {
            mmsDomain = isInternalStage ? "mms.sp-stage.net" : "mms.sp-prod.net";
        } else {
            mmsDomain = b.mmsDomain;
        }

        if (b.cmpDomain == null) {
            cmpDomain = isInternalStage ? "cmp.sp-stage.net" : "sourcepoint.mgr.consensu.org";
        } else {
            cmpDomain = b.cmpDomain;
        }

        if (b.viewGroup == null) {
            // render on top level activity view if no viewGroup specified
            View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            if (view instanceof ViewGroup) {
                viewGroup = (ViewGroup) view;
            } else {
                viewGroup = null;
                Log.e(TAG, "Current window not a ViewGroup can't render WebView");
            }
        } else {
            viewGroup = b.viewGroup;
        }

        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        euconsent = sharedPref.getString(EU_CONSENT_KEY, null);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);
    }

    public void run() {
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
                } {
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
        ViewGroup.LayoutParams webviewLayoutParams = new ViewGroup.LayoutParams(
                0,
                0);

        webView.setLayoutParams(webviewLayoutParams);
        webView.setBackgroundColor(Color.TRANSPARENT);

        MessageInterface mInterface = new MessageInterface();
        webView.addJavascriptInterface(mInterface, "JSReceiver");

        viewGroup.addView(webView);

        webView.getSettings().setJavaScriptEnabled(true);

        String href = getHref();
        if (href == null) {
            return;
        }

        String msgDomain;
        try {
            msgDomain = URLEncoder.encode(mmsDomain, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String cmpOrigin;
        try {
            cmpOrigin = URLEncoder.encode("//" + cmpDomain, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String targetingParamsJSON;
        try {
            targetingParamsJSON = URLEncoder.encode(targetingParams.toString(), "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        List<String> params = new ArrayList<>();

        params.add("_sp_accountId=" + String.valueOf(accountId));

        params.add("_sp_cmp_inApp=true");

        params.add("_sp_writeFirstPartyCookies=true");

        params.add("_sp_siteHref=" + href);

        params.add("_sp_msg_domain=" + msgDomain);

        params.add("_sp_cmp_origin=" + cmpOrigin);

        params.add("_sp_msg_targetingParams=" + targetingParamsJSON);

        params.add("_sp_debug_level=" + debugLevel.name());

        params.add("_sp_msg_stageCampaign=" + isStage);

        webView.loadUrl(inAppMessagingPageUrl + "?" + TextUtils.join("&", params));

        webView.setWebViewClient(new WebViewClient());
    }

    private String getHref() {
        String href = null;
        try {
            href = URLEncoder.encode("http://" + siteName + "/" + page, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return href;
    }

    // get site id corresponding to account id and site name. Read from local storage if present.
    // Write to local storage after getting response.
    private String getSiteId() {
        String siteIdKey = SP_SITE_ID + "_" + Integer.toString(accountId) + "_" + siteName;

        String storedSiteId = sharedPref.getString(siteIdKey, null);
        if (storedSiteId != null) {
            return storedSiteId;
        }
        String href = getHref();
        if (href == null) {
            return null;
        }

        String result = load("https://" + mmsDomain + "get_site_data?account_id=" + Integer.toString(accountId) + "&href=" + href);

        String siteId;
        try {
            siteId = new JSONObject(result).getString("site_id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(siteIdKey, siteId);
        editor.commit();

        return siteId;
    }

    public boolean getVendorConsent(String customVendorId) {
        return getVendorConsents(new String[]{customVendorId})[0];
    }

    public boolean[] getVendorConsents(String[] customVendorIds) {
        String CUSTOMER_VENDOR_PREFIX = SP_PREFIX + "_custom_vendor_consent_";
        boolean[] result = new boolean[customVendorIds.length];

        String siteId = getSiteId();
        if (siteId == null) {
            return result;
        }

        // read results from local storage if present first
        List<String> customVendorIdsToRequest = new ArrayList<>();
        for (int i = 0; i < customVendorIds.length; i++) {
            String customVendorId = customVendorIds[i];
            String storedConsentData = sharedPref.getString(CUSTOMER_VENDOR_PREFIX + customVendorId, null);
            if (storedConsentData == null) {
                customVendorIdsToRequest.add(customVendorId);
            } else {
                result[i] = Boolean.getBoolean(storedConsentData);
            }
        }

        if (customVendorIdsToRequest.size() == 0) {
            return result;
        }

        String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
        String euconsentParam = euconsent == null ? "[EUCONSENT]" : euconsent;
        String customVendorIdString = URLEncoder.encode(TextUtils.join(",", customVendorIdsToRequest));
        String response = load("https://" + cmpDomain + "/v2/consent/" + siteId + "/custom-vendors?customVendorIds=" + customVendorIdString + "&consent_uuid=" + consentParam + "&euconsent=" + euconsentParam);

        JSONArray consentedCustomVendors;
        try {
            JSONObject consentedCustomData = new JSONObject(response);
            consentedCustomVendors = consentedCustomData.getJSONArray("consentedVendors");
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        // write results to local storage after reading from endpoint
        for (int i = 0; i < consentedCustomVendors.length(); i++) {
            try {
                JSONObject consentedCustomVendor = consentedCustomVendors.getJSONObject(i);
                editor.putString(CUSTOMER_VENDOR_PREFIX + consentedCustomVendor.get("_id"), Boolean.toString(true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.commit();

        for (int i = 0; i < customVendorIds.length; i++) {
            String customVendorId = customVendorIds[i];
            String storedConsentData = sharedPref.getString(CUSTOMER_VENDOR_PREFIX + customVendorId, null);
            if (storedConsentData != null) {
                result[i] = Boolean.getBoolean(storedConsentData);
            }
        }

        return result;
    }

    private void finish() {
        if (onInteractionComplete != null) {
            onInteractionComplete.run(this);
        }

        viewGroup.removeView(webView);
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
                    } {
                        android.webkit.CookieSyncManager.getInstance().sync();
                    }

                    if (ConsentLib.this.onReceiveMessageData != null) {
                        ConsentLib.this.onReceiveMessageData.run(ConsentLib.this);
                    }

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
        public void onMessageChoiceSelect(int choiceType) {
            ConsentLib.this.choiceType = choiceType;

            if (ConsentLib.this.onMessageChoiceSelect != null) {
                ConsentLib.this.onMessageChoiceSelect.run(ConsentLib.this);
            }
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

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConsentLib.this.finish();
                }
            });
        }
    }
}

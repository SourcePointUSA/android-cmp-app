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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
        BuildStep setOnSendConsentData(Callback c);
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
        private Callback onSendConsentData = null;
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
        public BuildStep setOnSendConsentData(Callback c) {
            onSendConsentData = c;
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

    private final Activity activity;
    private final String siteName;
    private final int accountId;
    private final String page;
    private final ViewGroup viewGroup;
    private final Callback onReceiveMessageData;
    private final Callback onMessageChoiceSelect;
    private final Callback onSendConsentData;
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
    public String consentUUID = null;

    private ConsentLib(Builder b) {
        Log.i(TAG, "Instantiating consent lib");

        activity = b.activity;
        siteName = b.siteName;
        accountId = b.accountId;
        page = b.page;
        onReceiveMessageData = b.onReceiveMessageData;
        onMessageChoiceSelect = b.onMessageChoiceSelect;
        onSendConsentData = b.onSendConsentData;
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cm.getInstance().flush();
                } {
                    android.webkit.CookieSyncManager.getInstance().sync();
                }

                cm.setAcceptCookie(acceptCookie);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.setAcceptThirdPartyCookies(webView, true);
        }

        ViewGroup.LayoutParams webviewLayoutParams = new ViewGroup.LayoutParams(
                0,
                0);

        webView.setLayoutParams(webviewLayoutParams);
        webView.setBackgroundColor(Color.TRANSPARENT);

        MessageInterface mInterface = new MessageInterface(this);
        webView.addJavascriptInterface(mInterface, "JSReceiver");

        viewGroup.addView(webView);

        webView.getSettings().setJavaScriptEnabled(true);

        String href = null;
        try {
            href = URLEncoder.encode("http://" + siteName + "/" + page, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String msgDomain = null;
        try {
            msgDomain = URLEncoder.encode(mmsDomain, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String cmpOrigin = null;
        try {
            cmpOrigin = URLEncoder.encode("//" + cmpDomain, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String targetingParamsJSON = null;
        try {
            targetingParamsJSON = URLEncoder.encode(targetingParams.toString(), "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder requestUrl = new StringBuilder();

        requestUrl.append(inAppMessagingPageUrl);

        requestUrl.append("?");

        List<String> params = new ArrayList<>();

        params.add("_sp_accountId=" + String.valueOf(accountId));

        params.add("_sp_cmp_inApp=true");

        params.add("_sp_writeFirstPartyCookies=true");

        params.add("_sp_siteHref=" + href);

        params.add("_sp_msg_domain=" + msgDomain);

        params.add("_sp_cmp_origin=" + cmpOrigin);

        params.add("_sp_msg_targetingParams=" + targetingParamsJSON);

        params.add("_sp_debug_level=" + debugLevel.name());

        webView.loadUrl(inAppMessagingPageUrl + "?" + TextUtils.join("&", params));
        webView.setWebViewClient(new WebViewClient());
    }

    private class MessageInterface {

        private final ConsentLib consentLib;

        MessageInterface(ConsentLib c) {
            consentLib = c;
        }

        @JavascriptInterface
        public void onReceiveMessageData(final boolean willShowMessage, String _msgJSON) {
            consentLib.msgJSON = _msgJSON;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (willShowMessage) {
                        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.bringToFront();
                        webView.requestLayout();
                    } else {
                        viewGroup.removeView(webView);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cm.getInstance().flush();
                    } {
                        android.webkit.CookieSyncManager.getInstance().sync();
                    }

                    if (consentLib.onReceiveMessageData != null) {
                        consentLib.onReceiveMessageData.run(consentLib);
                    }
                }
            });
        }

        @JavascriptInterface
        public void onMessageChoiceSelect(int choiceType) {
            consentLib.choiceType = choiceType;

            if (consentLib.onMessageChoiceSelect != null) {
                consentLib.onMessageChoiceSelect.run(consentLib);
            }
        }

        @JavascriptInterface
        public void sendConsentData(final String euconsent, final String consentUUID) {
            SharedPreferences.Editor editor = sharedPref.edit();

            if (euconsent != null) {
                consentLib.euconsent = euconsent;
                editor.putString(EU_CONSENT_KEY, euconsent);
            }

            if (consentUUID != null) {
                consentLib.consentUUID = consentUUID;
                editor.putString(CONSENT_UUID_KEY, consentUUID);
            }

            if (euconsent != null || consentUUID != null) {
                editor.commit();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewGroup.removeView(webView);

                    if (consentLib.onSendConsentData != null) {
                        consentLib.onSendConsentData.run(consentLib);
                    }
                }
            });
        }
    }
}

package com.example.cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dmitrirabinowitz on 8/15/18.
 */

public class ConsentLib {

    public interface Callback {
        void run(ConsentLib c);
    }

    /*
     * Use step pattern to enforce ConsentLib is built with required parameters
     * https://www.javacodegeeks.com/2013/05/building-smart-builders.html
     */
    public static interface ActivityStep {
        public SiteNameStep setActivity(Activity a);
    }

    public static interface SiteNameStep {
        public BuildStep setSiteName(String s);
    }

    /*
     * Set all optional parameters here and build
     */
    public static interface BuildStep {
        public BuildStep setPage(String s);
        public BuildStep setViewGroup(ViewGroup v);
        public BuildStep setOnReceiveMessageData(Callback c);
        public BuildStep setOnMessageChoiceSelect(Callback c);
        public BuildStep setOnSendConsentData(Callback c);
        public ConsentLib build();
    }

    public static ActivityStep newBuilder() {
        return new Builder();
    }

    private static class Builder implements ActivityStep, SiteNameStep, BuildStep {
        private Activity activity;
        private String siteName;
        private String page = "";
        private ViewGroup viewGroup = null;
        private Callback onReceiveMessageData = null;
        private Callback onMessageChoiceSelect = null;
        private Callback onSendConsentData = null;

        public SiteNameStep setActivity(Activity a) {
            activity = a;
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
    private final String page;
    private final ViewGroup viewGroup;
    private final Callback onReceiveMessageData;
    private final Callback onMessageChoiceSelect;
    private final Callback onSendConsentData;

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
        page = b.page;
        onReceiveMessageData = b.onReceiveMessageData;
        onMessageChoiceSelect = b.onMessageChoiceSelect;
        onSendConsentData = b.onSendConsentData;

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

        String url = null;
        try {
            url = URLEncoder.encode("http://" + siteName + "/" + page, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        webView.loadUrl("http://10.0.2.2/dialogue.html?_sp_cmp_inApp=true&_sp_writeFirstPartyCookies=true&_sp_siteHref=" + url);
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

package com.example.cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by dmitrirabinowitz on 8/15/18.
 */

public class ConsentLib {

    public class Callbacks {

        void onMessageLoaded(boolean willShowMessage) {}

        void onConsentGiven(String euconsent, String consentUUID) {}

    }

    private Activity activity;
    private ViewGroup viewGroup = null;

    private static final String TAG = "ConsentLib";

    private static final String EU_CONSENT_KEY = "euconsent";

    private static final String CONSENT_UUID_KEY = "consentUUID";

    private final SharedPreferences sharedPref;

    private final Callbacks callbacks;

    private WebView webView;

    public ConsentLib(Activity a) {
        this(a, null, null);
    }

    public ConsentLib(Activity a, Callbacks c) {
        this(a, null, c);
    }

    public ConsentLib(Activity a, ViewGroup v) {
        this(a, v, null);
    }

    public ConsentLib(Activity a, ViewGroup v, Callbacks c) {
        Log.i(TAG, "Instantiating consent lib");
        activity = a;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        callbacks = c == null ? new Callbacks() : c;

        if (v == null) {
            View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            if (view instanceof ViewGroup) {
                viewGroup = (ViewGroup) view;
            } else {
                Log.e(TAG, "Current window not a ViewGroup can't render WebView");
                return;
            }
        } else {
            viewGroup = v;
        }
    }

    public void run() {
        Log.i(TAG, "Beginning consent lib run");
        final android.webkit.CookieManager cm = android.webkit.CookieManager.getInstance();
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

        MessageInterface mInterface = new MessageInterface();
        webView.addJavascriptInterface(mInterface, "JSReceiver");

        Log.i(TAG, "Adding webview to view group");
        viewGroup.addView(webView);
        Log.i(TAG, "View added");

        String euconsent = sharedPref.getString(EU_CONSENT_KEY, null);

        if (euconsent != null) {
            cm.setCookie("10.0.2.2", "euconsent=" + euconsent + "; Path=/; Expires=" + (60 * 60 * 24 * 364) + ";");
        }

        String consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);

        if (consentUUID != null) {
            cm.setCookie("10.0.2.2", "consentUUID=" + consentUUID + "; Path=/; Expires=" + (60 * 60 * 24 * 364) + ";");
        }

        webView.getSettings().setJavaScriptEnabled(true);
        Log.i(TAG, "Loading url");
        webView.loadUrl("http://10.0.2.2:9090/dialogue.html?_sp_cmp_inApp=true");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                Log.i(TAG, "page started loading");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Page finished loading");
            }
        });
    }

    private class MessageInterface {
        @JavascriptInterface
        public void onLoadMessage(final boolean willShowMessage) {
            Log.i(TAG, "On load message called");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "In ui thread");
                    if (willShowMessage) {
                        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        webView.bringToFront();
                        webView.requestLayout();
                    } else {
                        viewGroup.removeView(webView);
                    }
                    callbacks.onMessageLoaded(willShowMessage);
                }
            });
        }

        @JavascriptInterface
        public void sendConsentData(final String euconsent, final String consentUUID) {
            SharedPreferences.Editor editor = sharedPref.edit();

            if (euconsent != null) {
                editor.putString(EU_CONSENT_KEY, euconsent);
            }

            if (consentUUID != null) {
                editor.putString(CONSENT_UUID_KEY, consentUUID);
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewGroup.removeView(webView);
                    callbacks.onConsentGiven(euconsent, consentUUID);
                }
            });
        }
    }
}

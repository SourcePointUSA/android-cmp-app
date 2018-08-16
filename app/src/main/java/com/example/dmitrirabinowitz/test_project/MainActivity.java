package com.example.dmitrirabinowitz.test_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.cmplibrary.ConsentLib;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        ConsentLib cLib = new ConsentLib(this);
        cLib.run();
    }
}

//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//
//    private static final String EU_CONSENT_KEY = "euconsent";
//
//    private static final String CONSENT_UUID_KEY = "consentUUID";
//
//    private SharedPreferences sharedPref;
//
//    private WebView webView;
//
//    private LinearLayout linearLayout;
//
//    private void createLayout() {
//        linearLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//
//        webView = new WebView(this);
//
//        linearLayout.setBackgroundColor(Color.GREEN);
//
//        LinearLayout.LayoutParams webviewLayoutParams = new LinearLayout.LayoutParams(
//                1000,
//                800);
//
//        webView.setLayoutParams(webviewLayoutParams);
//        webView.setBackgroundColor(Color.TRANSPARENT);
//
//        MessageInterface mInterface = new MessageInterface();
//        webView.addJavascriptInterface(mInterface, "JSReceiver");
//
//        linearLayout.addView(webView);
//
//        setContentView(linearLayout, layoutParams);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG,"setting content view");
//
//        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//
//        createLayout();
//
//        showWebView();
//    }
//
//    void showWebView() {
//        android.webkit.CookieManager cm = android.webkit.CookieManager.getInstance();
//        cm.setAcceptCookie(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            cm.setAcceptThirdPartyCookies(webView, true);
//        }
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://10.0.2.2:9090/dialogue.html?_sp_cmp_inApp=true");
//        webView.setWebViewClient(new WebViewClient());
//    }
//
//    private class MessageInterface {
//        @JavascriptInterface
//        public void onLoadMessage(final boolean willShowMessage) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (willShowMessage) {
//                        LinearLayout.LayoutParams webviewLayoutParams = new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.MATCH_PARENT);
//
//                        webView.setLayoutParams(webviewLayoutParams);
//                    } else {
//                        linearLayout.removeView(webView);
//                    }
//                }
//            });
//        }
//
//        @JavascriptInterface
//        public void sendConsentData(String euconsent, String consentUUID) {
//            android.webkit.CookieManager.getInstance().flush();
//            Log.i(TAG, "Cookies for cmp after: " + android.webkit.CookieManager.getInstance().getCookie("cmp.sp-stage.net"));
//            Log.i(TAG, "Cookies for mms after: " + android.webkit.CookieManager.getInstance().getCookie("mms.sp-stage.net"));
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    linearLayout.removeView(webView);
//                }
//            });
//        }
//    }
//}

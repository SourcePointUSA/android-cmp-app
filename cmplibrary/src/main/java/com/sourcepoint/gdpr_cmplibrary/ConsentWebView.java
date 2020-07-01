package com.sourcepoint.gdpr_cmplibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.gdpr_cmplibrary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



abstract public class ConsentWebView extends WebView {

    private static final String TAG = "ConsentWebView";

    @SuppressWarnings("unused")
    private class JSReceiverInterface {

        private static final String TAG = "JSReceiverInterface";

        @JavascriptInterface
        public void log(String tag, String msg){
            Log.i(tag, msg);
        }

        @JavascriptInterface
        public void log(String msg){
            Log.i(TAG, msg);
        }

        // called when message is about to be shown
        @JavascriptInterface
        public void onConsentUIReady() {
            ConsentWebView.this.onConsentUIReady();
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onAction(int choiceType, String choiceId) {
            ConsentWebView.this.onAction(choiceType, choiceId != null ? Integer.parseInt(choiceId) : null);
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onSavePM(String payloadStr) throws JSONException, ConsentLibException {
            JSONObject payloadJson = new JSONObject(payloadStr);
            ConsentWebView.this.onSavePM(new GDPRUserConsent(payloadJson));
        }

        @JavascriptInterface
        public void onError(String errorMessage) {
            ConsentWebView.this.onError(new ConsentLibException("errorMessage"));
        }
    }

    public ConsentWebView(Context context) {
        super(getFixedContext(context));
        setup();
    }

    // Method created for avoiding crashes when inflating the webview on android Lollipop
    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.createConfigurationContext(context.getResources().getConfiguration());
        }
        return context;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
                enableSlowWholeDocumentDraw();
            }
        }
        getSettings().setJavaScriptEnabled(true);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.requestFocus();
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        view.evaluateJavascript(getFileContent(getResources().openRawResource(R.raw.js_receiver)),null);
                    }else
                    {
                        view.loadUrl("javascript:" + getFileContent(getResources().openRawResource(R.raw.js_receiver)));
                    }
                } catch (IOException e) {
                    ConsentWebView.this.onError(new ConsentLibException(e, "Unable to load jsReceiver into ConsentLibWebview."));
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError: " + error.toString());
                onError(new ConsentLibException.ApiException(error.toString()));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError: Error " + errorCode + ": " + description);
                onError(new ConsentLibException.ApiException(description));
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError: Error " + error);
                onError(new ConsentLibException.ApiException(error.toString()));
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                String message = "The WebView rendering process crashed!";
                Log.e(TAG, message);
                onError(new ConsentLibException(message));
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadLinkOnExternalBrowser(url);
                return true;
            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.getHitTestResult())));
                view.getContext().startActivity(browserIntent);
                return false;
            }
        });
        setOnKeyListener((view, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode ) {
                ConsentWebView.this.onBackPressAction();
                return true;
            }
            return false;
        });
        addJavascriptInterface(new JSReceiverInterface(), "JSReceiver");
    }

    abstract public void onConsentUIReady();

    abstract public void onError(ConsentLibException error);

    abstract public void onAction(int choiceType, Integer choiceId);

    abstract public void onSavePM(GDPRUserConsent GDPRUserConsent);

    abstract public void onBackPressAction();

    public void loadConsentUIFromUrl(String url) {
        Log.d(TAG, "Loading Webview with: " + url);
        Log.d(TAG, "User-Agent: " + getSettings().getUserAgentString());
        loadUrl(url);
    }

    private void loadLinkOnExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getContext().startActivity(intent);
    }

    private boolean doesLinkContainImage(HitTestResult testResult) {
        return testResult.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE;
    }

    private String getLinkUrl(HitTestResult testResult) {
        if (doesLinkContainImage(testResult)) {
            Handler handler = new Handler();
            Message message = handler.obtainMessage();
            requestFocusNodeHref(message);
            return (String) message.getData().get("url");
        }
        return testResult.getExtra();
    }


    private String getFileContent(InputStream is) throws IOException {

        BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8" ));
        StringBuilder sb = new StringBuilder();
        String line;
        while(( line = br.readLine()) != null ) {
            sb.append( line );
            sb.append( '\n' );
        }
        return sb.toString();
    }
}

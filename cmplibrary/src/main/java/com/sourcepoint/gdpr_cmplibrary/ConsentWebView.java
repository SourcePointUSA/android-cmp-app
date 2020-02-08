package com.sourcepoint.gdpr_cmplibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
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
import java.util.HashSet;

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
        public void onAction(int choiceType, Integer choiceId) {
            ConsentWebView.this.onAction(choiceType, choiceId);
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

    // A simple mechanism to keep track of the urls being loaded by the WebView
    private class ConnectionPool {
        private HashSet<String> connections;
        private static final String INITIAL_LOAD = "data:text/html,";

        ConnectionPool() {
            connections = new HashSet<>();
        }

        void add(String url) {
            // on API level < 21 the initial load is not recognized by the WebViewClient#onPageStarted callback
            if (url.equalsIgnoreCase(ConnectionPool.INITIAL_LOAD)) return;
            connections.add(url);
        }

        void remove(String url) {
            connections.remove(url);
        }

        boolean contains(String url) {
            return connections.contains(url);
        }
    }

    private long timeoutMillisec;
    private ConnectionPool connectionPool;

    public static long DEFAULT_TIMEOUT = 10000;

    public ConsentWebView(Context context, long timeoutMillisec) {
        super(context);
        this.timeoutMillisec = timeoutMillisec;
        connectionPool = new ConnectionPool();
        setup();
    }

    public ConsentWebView(Context context) {
        super(context);
        this.timeoutMillisec = DEFAULT_TIMEOUT;
        connectionPool = new ConnectionPool();
        setup();
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

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
                enableSlowWholeDocumentDraw();
            }
        }
        CookieManager.getInstance().setAcceptCookie(false);
        getSettings().setAppCacheEnabled(false);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setSupportZoom(false);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportMultipleWindows(true);
        getSettings().setDomStorageEnabled(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                connectionPool.add(url);
                Runnable run = () -> {
                    if (connectionPool.contains(url))
                        onError(new ConsentLibException.ApiException("TIMED OUT: " + url));
                };
                Handler myHandler = new Handler(Looper.myLooper());
                myHandler.postDelayed(run, timeoutMillisec);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                connectionPool.remove(url);
                //view.loadUrl("javascript:" + "addEventListener('message', SDK.onEvent('oie'))");
                try {
                    view.loadUrl("javascript:" + getFileContent(getResources().openRawResource(R.raw.js_receiver)));
                } catch (IOException e) {
                    ConsentWebView.this.onError(new ConsentLibException(e, "Unable to load jsReceiver into ConasentLibWebview."));
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
            WebView webView = (WebView) view;
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    KeyEvent.KEYCODE_BACK == keyCode &&
                    webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return false;
        });
        addJavascriptInterface(new JSReceiverInterface(), "JSReceiver");
        resumeTimers();
    }

    boolean hasLostInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    abstract public void onConsentUIReady();

    abstract public void onError(ConsentLibException error);

    abstract public void onAction(int choiceType, Integer choiceId);

    abstract public void onSavePM(GDPRUserConsent GDPRUserConsent);


    public void loadConsentUIFromUrl(String url) {
        if (hasLostInternetConnection())
            ConsentWebView.this.onError(new ConsentLibException.NoInternetConnectionException());
        Log.d(TAG, "Loading Webview with: " + url);
        Log.d(TAG, "User-Agent: " + getSettings().getUserAgentString());
        loadUrl(url);
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

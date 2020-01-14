package com.sourcepoint.cmplibrary;

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
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

abstract public class ConsentWebView extends WebView {
    private static final String TAG = "ConsentWebView";

    private static final String pmBaseUrl = "https://ccpa-inapp-pm.sp-prod.net/?privacy_manager_id=5df10416fb59af14237b09c8&site_id=5739&ccpa_origin=https://ccpa-service.sp-prod.net&";



    // TODO: pass this script to a .js file and return it as a string in this method
    private String getJSInjection(){
        return "addEventListener('message', handleEvent);\n" +
                "function handleEvent(event) {\n" +
                "    try {\n" +
                "        JSReceiver.log(JSON.stringify(event.data, null, 2));\n" +
                "        if (event.data.name === 'sp.showMessage') {\n" +
                "            JSReceiver.onMessageReady();\n" +
                "            return;\n" +
                "        }\n" +
                "        const data = eventData(event);\n" +
                "        JSReceiver.log(JSON.stringify(data, null, 2));\n" +
                "        if(data.type) {\n" +
                "            if(data.type === 1) JSReceiver.onSavePM(JSON.stringify(data.payload));\n" +
                "            else JSReceiver.onAction(data.type);\n" +
                "        }\n" +
                "    } catch (err) {\n" +
                "        JSReceiver.log(err.stack);\n" +
                "    };\n" +
                "};\n" +
                "\n" +
                "function eventData(event) {\n" +
                "    return isFromPM(event) ? dataFromPM(event) : dataFromMessage(event);\n" +
                "};\n" +
                "\n" +
                "function isFromPM(event) {\n" +
                "    return !!event.data.payload;\n" +
                "};\n" +
                "\n" +
                "function dataFromMessage(msgEvent) {\n" +
                "    return {\n" +
                "        name: msgEvent.data.name,\n" +
                "        type: msgEvent.data.actions.length ? msgEvent.data.actions[0].data.type : null\n" +
                "    };\n" +
                "};\n" +
                "\n" +
                "function dataFromPM(pmEvent) {\n" +
                "    const data = {\n" +
                "        name: pmEvent.data.name,\n" +
                "        type: pmEvent.data ? pmEvent.data.payload.actionType : null,\n" +
                "    };\n" +
                "    if(data.type === 1) data.payload = userConsents(pmEvent.data.payload);\n" +
                "    return data;\n" +
                "};\n" +
                "\n" +
                "function userConsents(payload){\n" +
                "    return {\n" +
                "        rejectedVendors: payload.consents.vendors.rejected,\n" +
                "        rejectedCategories: payload.consents.categories.rejected\n" +
                "    }\n" +
                "}";
    }

    @SuppressWarnings("unused")
    private class MessageInterface {

        @JavascriptInterface
        public void log(String tag, String msg){
            Log.i(tag, msg);
        }

        @JavascriptInterface
        public void log(String msg){
            Log.i("JS", msg);
        }

        // called when message is about to be shown
        @JavascriptInterface
        public void onMessageReady() {
            Log.d("onMessageReady", "called");
            ConsentWebView.this.flushOrSyncCookies();
            ConsentWebView.this.onMessageReady();
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onAction(int choiceType) {
            Log.d("onAction", "called");
            if (ConsentWebView.this.hasLostInternetConnection()) {
                ConsentWebView.this.onError(new ConsentLibException.NoInternetConnectionException());
            }
            ConsentWebView.this.onAction(choiceType);
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onSavePM(String payloadStr) throws JSONException {
            Log.d("onSavePM", "called");
            if (ConsentWebView.this.hasLostInternetConnection()) {
                ConsentWebView.this.onError(new ConsentLibException.NoInternetConnectionException());
            }
            JSONObject payloadJson = new JSONObject(payloadStr);
            ConsentWebView.this.onSavePM(
                    new UserConsent(
                            payloadJson.getJSONArray("rejectedVendors"),
                            payloadJson.getJSONArray("rejectedCategories")
                    )
            );
        }

        //called when an error is occured while loading web-view
        @JavascriptInterface
        public void onError(String errorType) {
            ConsentLibException error = ConsentWebView.this.hasLostInternetConnection() ?
                    new ConsentLibException.NoInternetConnectionException() :
                    new ConsentLibException("Something went wrong in the javascript world.");
            ConsentWebView.this.onError(error);
        }
        // xhr logger
        @JavascriptInterface
        public void xhrLog(String response){
            Log.d("xhrLog" , response);
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
    private boolean isShowPM = false;

    public static long DEFAULT_TIMEOUT = 10000;

    public ConsentWebView(Context context, long timeoutMillisec, boolean isShowPM) {
        super(context);
        this.timeoutMillisec = timeoutMillisec;
        connectionPool = new ConnectionPool();
        this.isShowPM = isShowPM;
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
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);
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
                flushOrSyncCookies();
                connectionPool.remove(url);
                //view.loadUrl("javascript:" + "addEventListener('message', SDK.onEvent('oie'))");
                view.loadUrl("javascript:" + getJSInjection());
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
        addJavascriptInterface(new MessageInterface(), "JSReceiver");
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

    private void flushOrSyncCookies() {
        // forces the cookies sync between RAM and local storage
        // https://developer.android.com/reference/android/webkit/CookieSyncManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().flush();
        else CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        flushOrSyncCookies();
    }

    abstract public void onMessageReady();

    abstract public void onError(ConsentLibException error);

    abstract public void onAction(int choiceType);

    abstract public void onSavePM(UserConsent userConsent);


    public void loadConsentMsgFromUrl(String url) throws ConsentLibException.NoInternetConnectionException {
        if (hasLostInternetConnection())
            throw new ConsentLibException.NoInternetConnectionException();

        // On API level >= 21, the JavascriptInterface is not injected on the page until the *second* page load
        // so we need to issue blank load with loadData
        loadData("", "text/html", null);
        Log.d(TAG, "Loading Webview with: " + url);
        Log.d(TAG, "User-Agent: " + getSettings().getUserAgentString());
        loadUrl(url);
    }

    private String pmUrl() {
        return pmBaseUrl;
    }

    public boolean goBackIfPossible() {
        post(new Runnable() {
            @Override
            public void run() {
                if (canGoBack()) goBack();
            }
        });
        return canGoBack();
    }



    public void display() {
        setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        bringToFront();
        requestLayout();
    }
}

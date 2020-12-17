package com.sourcepoint.gdpr_cmplibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.gdpr_cmplibrary.R;

import com.sourcepoint.gdpr_cmplibrary.exception.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.*;

abstract public class ConsentWebView extends WebView {

    private static final String TAG = "ConsentWebView";
    private ConnectivityManager connectivityManager;
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

        // called when message or pm is about to be shown
        @JavascriptInterface
        public void onConsentUIReady( boolean isFromPM) {
            ConsentWebView.this.onConsentUIReady(isFromPM);
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onAction(String actionData) {
            try {
                ConsentWebView.this.onAction(consentAction(getJson(actionData, getLogger()), getLogger()));
            } catch (ConsentLibException e) {
                ConsentWebView.this.onError(e);
            }
        }

        @JavascriptInterface
        public void onError(String errorMessage) {
            ConsentWebView.this.onError(new ConsentLibException(errorMessage));
            getLogger().error(new RenderingAppException(errorMessage, errorMessage));
        }

        private ConsentAction consentAction(JSONObject actionFromJS, Logger logger) throws ConsentLibException {
            return new ConsentAction(
                    getInt("actionType", actionFromJS, getLogger()),
                    getString("choiceId", actionFromJS, logger),
                    getString("privacyManagerId", actionFromJS, logger),
                    getString("pmTab", actionFromJS, logger),
                    getBoolean("requestFromPm", actionFromJS, getLogger()),
                    getJson("saveAndExitVariables", actionFromJS, getLogger()),
                    getString("consentLanguage", actionFromJS, logger)
            );
        }
    }

    protected abstract Logger getLogger();

    public ConsentWebView(Context context) {
        super(getFixedContext(context));
        this.connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    view.loadUrl("javascript:" + getFileContent(getResources().openRawResource(R.raw.js_receiver)));
                } catch (IOException e) {
                    ConsentWebView.this.onError(new ConsentLibException(e, "Unable to load jsReceiver into ConasentLibWebview."));
                    getLogger().error(new UnableToLoadJSReceiverException(e, "Unable to load jsReceiver into ConasentLibWebview."));
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError: " + error.toString());
                onError(new ConsentLibException.ApiException(error.toString()));
                getLogger().error(new UnableToLoadJSReceiverException(error.toString()));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError: Error " + errorCode + ": " + description);
                onError(new ConsentLibException.ApiException(description));
                getLogger().error(new UnableToLoadJSReceiverException(description));
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError: Error " + error);
                onError(new ConsentLibException.ApiException(error.toString()));
                getLogger().error(new UnableToLoadJSReceiverException(error.toString()));
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                String message = "The WebView rendering process crashed!";
                Log.e(TAG, message);
                onError(new ConsentLibException(message));
                getLogger().error(new WebViewException(message));
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
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                loadLinkOnExternalBrowser(getLinkUrl(view.getHitTestResult()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.getHitTestResult())));
                view.getContext().startActivity(browserIntent);
                return false;
            }
        });

        addJavascriptInterface(new JSReceiverInterface(), "JSReceiver");
    }

    abstract public void onConsentUIReady(boolean isFromPM);

    abstract public void onError(ConsentLibException error);

    abstract public void onNoIntentActivitiesFoundFor(String url);

    abstract public void onAction(ConsentAction action);

    public void loadConsentUIFromUrl(String url) throws ConsentLibException {
        if (hasLostInternetConnection())
            throw new ConsentLibException.NoInternetConnectionException();

        Log.d(TAG, "Loading Webview with: " + url);
        Log.d(TAG, "User-Agent: " + getSettings().getUserAgentString());
        loadUrl(url);
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

    private void loadLinkOnExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager m = getContext().getPackageManager();
        List<ResolveInfo> l = m.queryIntentActivities(intent, m.MATCH_DEFAULT_ONLY);
        if(l.size() != 0) getContext().startActivity(intent);
        else onNoIntentActivitiesFoundFor(url);
    }

    private boolean hasLostInternetConnection() {
        if (this.connectivityManager == null) {
            return true;
        }
        NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }
}

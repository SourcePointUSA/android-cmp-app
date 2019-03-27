package com.sourcepoint.cmplibrary;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

abstract class ConsentWebView extends WebView {
    private static final String TAG = "ConsentWebView";
    @SuppressWarnings("unused")
    private class MessageInterface {
        // called when message loads, brings the WebView to the front when the message is ready
        @JavascriptInterface
        public void onReceiveMessageData(boolean willShowMessage, String _msgJSON) {
            ConsentWebView.this.flushOrSyncCookies();
            ConsentWebView.this.onMessageReady(willShowMessage);
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onMessageChoiceSelect(int choiceType) {
            if(ConsentWebView.this.hasLostInternetConnection()) {
                ConsentWebView.this.onErrorOccurred(new ConsentLibException.NoInternetConnectionException());
            }
            ConsentWebView.this.onMessageChoiceSelect(choiceType);
        }

        // called when interaction with message is complete
        @JavascriptInterface
        public void sendConsentData(String euconsent, String consentUUID) {
            ConsentWebView.this.onInteractionComplete(euconsent, consentUUID);
        }

        @JavascriptInterface
        public void onErrorOccurred(String errorType) {
            ConsentLibException error = ConsentWebView.this.hasLostInternetConnection() ?
                    new ConsentLibException.NoInternetConnectionException() :
                    new ConsentLibException("Something went wrong in the javascript world.");
            ConsentWebView.this.onErrorOccurred(error);
        }
    }

    public ConsentWebView(Context context) {
        super(context);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        CookieManager.getInstance().setAcceptCookie(true);

        addJavascriptInterface(new MessageInterface(), "JSReceiver");
        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportMultipleWindows(true);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }
        });

        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                WebView webView = (WebView) view;
                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        KeyEvent.KEYCODE_BACK == keyCode &&
                        webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    boolean hasLostInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) { return true; }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    private void flushOrSyncCookies() {
        // forces the cookies sync between RAM and local storage
        // https://developer.android.com/reference/android/webkit/CookieSyncManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        flushOrSyncCookies();
    }

    abstract public void onMessageReady(boolean willShowMessage);
    abstract public void onErrorOccurred(ConsentLibException error);
    abstract public void onInteractionComplete(String euConsent, String consentUUID);
    abstract public void onMessageChoiceSelect(int choiceType);

    public void loadMessage(String messageUrl) throws ConsentLibException.NoInternetConnectionException {
        if(hasLostInternetConnection()) throw new ConsentLibException.NoInternetConnectionException();

        loadData("", "text/html", null);
        Log.d(TAG, "Loading Webview with: "+messageUrl);
        Log.d(TAG, "User-Agent: "+getSettings().getUserAgentString());
        loadUrl(messageUrl);
    }

    public void display() {
        setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        bringToFront();
        requestLayout();
    }
}

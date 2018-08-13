package com.example.dmitrirabinowitz.test_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String PREF_FILE_KEY = "sp.mgr.consensu.pref";

    private static final String EU_CONSENT_KEY = "euconsent";

    private static final String CONSENT_UUID_KEY = "consentUUID";

    private SharedPreferences sharedPref;

    private WebView webView;

    private void createLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        webView = new WebView(this);

        webView.setLayoutParams(layoutParams);

        linearLayout.addView(webView);

        setContentView(linearLayout, layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"setting content view");

        sharedPref = getSharedPreferences(
                PREF_FILE_KEY, Context.MODE_PRIVATE);

        createLayout();

        String euconsent = sharedPref.getString(EU_CONSENT_KEY, null);

        if (euconsent != null) {
            Log.i(TAG, "reading euconsent from storage: " + euconsent);
        } else {
            Log.i(TAG, "euconsent is null");
        }

        String consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);

        if (consentUUID != null) {
            Log.i(TAG, "reading consentUUID from storage: " + consentUUID);
        } else {
            Log.i(TAG, "consentUUID is null");
        }

        final PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        List<HttpCookie> cookies = cookieStore.getCookies();
        Log.i(TAG, "cookies in store: " + cookies.size());
        for (HttpCookie cookie : cookies) {
            Log.i(TAG, "Cookie in store: " + cookie.getName() + " value: " + cookie.getValue());
        }
        CookieManager manager = new CookieManager( cookieStore, CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault( manager  );

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:7001/mms/get_message_json?fqdn=dev.local&v=1&account_id=22&abp=false&referrer=&session_referrer=&session_message_count=2&jv=2.0.1075&cdc=window._sp_.msg._internal.cdc1&href=http%3A%2F%2Fdev.local%2Fdialogue.html%3F_sp_debug_level%3Ddebug&t%5Ba%5D=b&t%5Bc%5D=d";

        //8ac2f538-5a19-4aa1-a262-25eeb920395b
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(TAG, "Received response: " + response);
                            List<HttpCookie> cookies = cookieStore.getCookies();
                            Log.i(TAG, "cookies in store after request: " + cookies.size());
                            for (HttpCookie cookie : cookies) {
                                Log.i(TAG, "Cookie in store after request: " + cookie.getName() + " value: " + cookie.getValue());
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing json: " + e.getLocalizedMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Received error: " + error.getLocalizedMessage(), error);
            }
        });

// Request a string response from the provided URL.
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://cmp.sp-stage.net/consent/v2/11/logic?hasConsentData&consentedToAny&consentedToAll&rejectedAny",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,"RECEIVED RESPONSE FROM CMP: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Received error: " + error.getLocalizedMessage(), error);
            }
        });

        queue.add(stringRequest);
        queue.add(stringRequest2);

        showWebView(cookieStore);
    }

    void showWebView(PersistentCookieStore cookieStore) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://pm.cmp.sp-stage.net/?privacy_manager_id=5b5b2cb90224020031755a12&site_id=674&consent_origin=https%3A%2F%2Fcmp.sp-stage.net&debug_level=DEBUG&in_app=true");

        TestViewClient testViewClient = new TestViewClient(cookieStore);
        webView.setWebViewClient(testViewClient);
    }

    private class TestViewClient extends WebViewClient {
        PersistentCookieStore _cookieStore;
        TestViewClient(PersistentCookieStore cookieStore) {
            _cookieStore = cookieStore;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handleWebViewInteraction(url);
            return true;
        }

        private void handleWebViewInteraction(String url) {
            handleReceivedConsentString(url);
            finish();
        }

        private void handleReceivedConsentString(String url) {
            Log.i(TAG, "Received consent string: " + url);
            String[] values = new String[0];

            if (url != null) {
                values = url.split("consent://");
            }

            if (values.length > 1) {
                String consentParams = values[1];
                String[] consentParamsArr = consentParams.split("/consentUUID/");
                if (values.length == 2) {
                    String euconsent = consentParamsArr[0];
                    String consentUUID = consentParamsArr[1];
                    SharedPreferences.Editor editor = sharedPref.edit();
                    HttpCookie euCookie = new HttpCookie("euconsent", euconsent);
                    HttpCookie consentCookie = new HttpCookie("consentUUID", consentUUID);
                    euCookie.setDomain("cmp.sp-stage.net");
                    euCookie.setMaxAge(100000);
                    consentCookie.setDomain("cmp.sp-stage.net");
                    consentCookie.setMaxAge(100000);
                    try {
                        _cookieStore.add(new URI(euCookie.getDomain()), euCookie);
                        _cookieStore.add(new URI(consentCookie.getDomain()), consentCookie);
                    } catch (Exception e) {
                        Log.e(TAG, "exception from generating uri: " + e.getLocalizedMessage(), e);
                    }

                    editor.putString(EU_CONSENT_KEY, euconsent);
                    editor.putString(CONSENT_UUID_KEY, consentUUID);

                    editor.commit();
                }
            }
        }
    }
}

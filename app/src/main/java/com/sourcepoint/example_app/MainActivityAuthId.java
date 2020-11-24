package com.sourcepoint.example_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityAuthId extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth_id);

        WebView wv = findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);

        SharedPreferences sharedPref = this.getSharedPreferences("myshared", Context.MODE_PRIVATE);
        String authId = sharedPref.getString("MyAppsAuthId", "");


        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                WebViewUtils.setAuthId(authId, view);
                super.onPageStarted(view, url, favicon);
            }
        });

        wv.loadUrl("https://carmelo-iriti.github.io/authid.github.io");
    }
}
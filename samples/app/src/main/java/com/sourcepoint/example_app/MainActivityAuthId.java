package com.sourcepoint.example_app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.sourcepoint.example_app.core.DataProvider;
import com.sourcepoint.gdpr_cmplibrary.WebViewUtils;

import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityAuthId extends AppCompatActivity {

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth_id);

        WebView wv = findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);

        String authId = dataProvider.getValue().getAuthId();
        String url = dataProvider.getValue().getUrl();

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                WebViewUtils.setAuthId(authId, view);
                super.onPageStarted(view, url, favicon);
            }
        });

        wv.loadUrl(url);
    }
}
package com.example.dmitrirabinowitz.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.sourcepoint.cmplibrary.ConsentLib;

public class ConsentActivity extends AppCompatActivity {
    static ConsentLib consentLib;
    static ConsentActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        ViewGroup container = getWindow().getDecorView().findViewById(android.R.id.content);
        WebView webView = consentLib.webView;
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.bringToFront();
        container.addView(webView);
        webView.requestLayout();
        instance = this;
    }
}

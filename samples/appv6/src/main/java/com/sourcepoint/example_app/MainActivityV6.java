package com.sourcepoint.example_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.cmplibrary.SPMessage;
import com.sourcepoint.cmplibrary.SpClient;
import com.sourcepoint.cmplibrary.SpConsentLib;
import com.sourcepoint.cmplibrary.creation.FactoryKt;
import com.sourcepoint.cmplibrary.model.*;
import com.sourcepoint.example_app.core.DataProvider;
import kotlin.Lazy;
import org.jetbrains.annotations.NotNull;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityV6 extends AppCompatActivity {

    private static final String TAG = "**MainActivity";

    private final GDPRCampaign gdpr = new GDPRCampaign(
            22,
            10589,
            "https://unified.mobile.demo",
            "404472"
    );

    private final CCPACampaign ccpa = new CCPACampaign(
            22,
            10589,
            "https://unified.mobile.demo",
            "404472"
    );

    private SpConsentLib gdprConsent = null;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gdprConsent = FactoryKt.makeConsentLib(gdpr, ccpa, this, PrivacyManagerTabK.FEATURES);
        gdprConsent.setSpClient(new LocalClient());
        findViewById(R.id.review_consents).setOnClickListener(_v -> gdprConsent.loadGDPRPrivacyManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
        gdprConsent.loadMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gdprConsent.dispose();
    }

    class LocalClient implements SpClient {

        @Override
        public void onMessageReady(@NotNull SPMessage message) {

        }

        @Override
        public void onError(@NotNull Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void onConsentReady(@NotNull SPConsents c) {
            for (String line : c.getGdpr().toString().split("\n"))
                Log.i(TAG, line);
        }

        @Override
        public void onUIFinished(@NotNull View v) {
            gdprConsent.removeView(v);
        }

        @Override
        public void onUIReady(@NotNull View v) {
            gdprConsent.showView(v);
        }

        @Override
        public void onAction(View view, @NotNull ActionType actionType) {
            Log.i(TAG, "ActionType: " + actionType.toString());
        }
    }
}
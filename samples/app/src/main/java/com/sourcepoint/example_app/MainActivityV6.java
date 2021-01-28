package com.sourcepoint.example_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.cmplibrary.Account;
import com.sourcepoint.cmplibrary.Builder;
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib;
import com.sourcepoint.cmplibrary.legislation.gdpr.SpGDPRClient;
import com.sourcepoint.example_app.core.DataProvider;
import com.sourcepoint.gdpr_cmplibrary.ActionTypes;
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;
import kotlin.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityV6 extends AppCompatActivity {

    private static final String TAG = "**MainActivity";

    private Account account = new Account(
            22,
            7639,
            "tcfv2.mobile.webview",
            "122058"
    );

    GDPRConsentLib gdprConsent = null;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gdprConsent = new Builder()
                .setAccountId(account.accountId)
                .setPropertyName(account.propertyName)
                .setPropertyId(account.propertyId)
                .setPmId(account.pmId)
                .setContext(this)
                .setAuthId(Objects.requireNonNull(dataProvider.getValue().getAuthId()))
                .build(GDPRConsentLib.class);

        gdprConsent.setSpGdprClient(new GdprClient());

        findViewById(R.id.review_consents).setOnClickListener(_v -> gdprConsent.loadPrivacyManager());
        findViewById(R.id.auth_id_activity).setOnClickListener(_v -> startActivity(new Intent(this, MainActivityAuthId.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        gdprConsent.loadMessage();
    }


    class GdprClient implements SpGDPRClient {
        @Override
        public void onConsentReadyCallback(@Nullable GDPRUserConsent consent) {
            for (String line : consent.toString().split("\n"))
                Log.i(TAG, line);
        }

        @Override
        public void onConsentUIFinishedCallback(@NotNull View v) {
            gdprConsent.removeView(v);
        }

        @Override
        public void onConsentUIReadyCallback(@NotNull View v) {
            gdprConsent.showView(v);
        }

        @Override
        public void onErrorCallback(@Nullable ConsentLibException error) {
            Log.e(TAG, "Something went wrong");
        }

        @Override
        public void onActionCallback(@Nullable ActionTypes actionTypes) {
            Log.i(TAG, "ActionType: " + actionTypes.toString());
        }
    }
}
package com.sourcepoint.example_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.model.CCPAUserConsent;
import com.sourcepoint.cmplibrary.model.Campaign;
import com.sourcepoint.cmplibrary.SpClient;
import com.sourcepoint.cmplibrary.creation.Builder;
import com.sourcepoint.example_app.core.DataProvider;
import com.sourcepoint.gdpr_cmplibrary.ActionTypes;
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;
import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK;
import kotlin.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityV6 extends AppCompatActivity {

    private static final String TAG = "**MainActivity";

    private final Campaign campaign = new Campaign(
            22,
            7639,
            "tcfv2.mobile.webview",
            "122058"
    );

    private ConsentLib gdprConsent = null;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gdprConsent = new Builder()
                .setAccountId(campaign.accountId)
                .setPropertyName(campaign.propertyName)
                .setPropertyId(campaign.propertyId)
                .setPmId(campaign.pmId)
                .setContext(this)
                .setAuthId(Objects.requireNonNull(dataProvider.getValue().getAuthId()))
                .build();

        gdprConsent.setSpClient(new LocalClient());

        findViewById(R.id.review_consents).setOnClickListener(_v -> gdprConsent.loadPrivacyManager());
        findViewById(R.id.auth_id_activity).setOnClickListener(_v -> startActivity(new Intent(this, MainActivityAuthId.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        gdprConsent.loadMessage();
    }

    View view = null;

    @Override
    public void onBackPressed() {
        if(view!= null){
            gdprConsent.removeView(view);
        }
    }

    class LocalClient implements SpClient {

        @Override
        public void onConsentReadyCallback(@NotNull CCPAUserConsent c) {

        }

        @Override
        public void onConsentReady(@Nullable GDPRUserConsent consent) {
            for (String line : consent.toString().split("\n"))
                Log.i(TAG, line);
        }

        @Override
        public void onConsentUIFinished(@NotNull View v) {
            gdprConsent.removeView(v);
        }

        @Override
        public void onConsentUIReady(@NotNull View v) {
            view = v;
            gdprConsent.showView(v);
        }

        @Override
        public void onError(@NotNull ConsentLibExceptionK error) {
            Log.e(TAG, "Something went wrong");
        }

        @Override
        public void onAction(@Nullable ActionTypes actionTypes) {
            Log.i(TAG, "ActionType: " + actionTypes.toString());
        }
    }
}
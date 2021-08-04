package com.sourcepoint.app.v6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.app.v6.core.DataProvider;
import com.sourcepoint.cmplibrary.SpConsentLib;
import com.sourcepoint.cmplibrary.UnitySpClient;
import com.sourcepoint.cmplibrary.creation.FactoryKt;
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder;
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv;
import com.sourcepoint.cmplibrary.exception.CampaignType;
import com.sourcepoint.cmplibrary.model.MessageLanguage;
import com.sourcepoint.cmplibrary.model.PMTab;
import com.sourcepoint.cmplibrary.model.exposed.*;
import com.sourcepoint.cmplibrary.util.SpUtils;
import kotlin.Lazy;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityJava extends AppCompatActivity {

    private static final String TAG = "**MainActivity";

    private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addMessageTimeout(5000)
            .addCampaignsEnv(CampaignsEnv.PUBLIC)
            .addCampaign(new SpCampaign(CampaignType.GDPR, Collections.emptyList()))
            .addCampaign(CampaignType.GDPR)
//            .addCampaign(CampaignType.CCPA, Arrays.asList(new TargetingParam("location", "US")))
            .build();

    private SpConsentLib spConsentLib = null;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spConsentLib = FactoryKt.makeConsentLib(
                spConfig,
                this,
                new LocalClient()
        );
        findViewById(R.id.review_consents_gdpr).setOnClickListener(_v ->
                spConsentLib.loadPrivacyManager(
                        "488393",//"13111",
                        PMTab.PURPOSES,
                        CampaignType.GDPR
                ));
        findViewById(R.id.review_consents_ccpa).setOnClickListener(_v ->
                spConsentLib.loadPrivacyManager(
                        "14967",
                        PMTab.PURPOSES,
                        CampaignType.CCPA
                ));
        findViewById(R.id.clear_all).setOnClickListener(_v ->
                SpUtils.clearAllData(this)
        );
        findViewById(R.id.auth_id_activity).setOnClickListener(_v ->
                startActivity(new Intent(this, MainActivityAuthId.class))
        );
        findViewById(R.id.custom_consent).setOnClickListener(_v ->
                spConsentLib.customConsentGDPR(
                        Arrays.asList("5ff4d000a228633ac048be41"),
                        Arrays.asList("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                        new ArrayList<>(),
                        (SPConsents) -> {  return Unit.INSTANCE;  }
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        spConsentLib.loadMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        spConsentLib.dispose();
    }

    class LocalClient implements UnitySpClient {

        @Override
        public void onMessageReady(@NotNull JSONObject message) {

        }

        @Override
        public void onError(@NotNull Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void onConsentReady(@NotNull SPConsents c) {
            System.out.println("onConsentReady: " + c);
        }

        @Override
        public void onConsentReady(@NotNull String consent) {
            System.out.println("onConsentReady String: " + consent);
        }

        @Override
        public void onConsentReady(@NotNull SPConsents consent, boolean fromPm) {
            System.out.println("onConsentReady String: " + consent);
        }

        @Override
        public void onUIFinished(@NotNull View v) {
            spConsentLib.removeView(v);
        }

        @Override
        public void onUIReady(@NotNull View v) {
            spConsentLib.showView(v);
        }

        @Override
        public void onAction(View view, @NotNull ActionType actionType) {
            Log.i(TAG, "ActionType: " + actionType.toString());
        }
    }
}
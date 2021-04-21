package com.sourcepoint.app.v6;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.cmplibrary.SpClient;
import com.sourcepoint.cmplibrary.SpConsentLib;
import com.sourcepoint.cmplibrary.UnitySpClient;
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder;
import com.sourcepoint.cmplibrary.model.exposed.*;
import com.sourcepoint.cmplibrary.creation.FactoryKt;
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv;
import com.sourcepoint.cmplibrary.exception.Legislation;
import com.sourcepoint.cmplibrary.model.*;
import com.sourcepoint.app.v6.core.DataProvider;
import kotlin.Lazy;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivityV6 extends AppCompatActivity {

    private static final String TAG = "**MainActivity";

    private final SpCampaign gdprCampaign = new SpCampaign(
            Legislation.GDPR,
            CampaignEnv.PUBLIC,
            new TargetingParam[]{
                    new TargetingParam("location", "EU")
            }
    );

    private final SpCampaign ccpaCamapign = new SpCampaign(
            Legislation.CCPA,
            CampaignEnv.PUBLIC,
            new TargetingParam[]{
                    new TargetingParam("location", "EU")
            }
    );

    private final SpConfig spConfig = new SpConfig(
            22,
            "mobile.multicampaign.demo",
            new SpCampaign[]{
                    ccpaCamapign,
                    gdprCampaign
            }
    );

    private final TargetingParam[] targetingParamsGdpr = new TargetingParam[]{new TargetingParam("location", "EU")};
    private final TargetingParam[] targetingParamsCcpa = new TargetingParam[]{new TargetingParam("location", "EU")};

    private final SpConfig spConfig2 = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addCampaign(Legislation.CCPA, CampaignEnv.PUBLIC, targetingParamsGdpr)
            .addCampaign(Legislation.GDPR, CampaignEnv.PUBLIC, targetingParamsCcpa)
            .build();

    private SpConsentLib gdprConsent = null;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gdprConsent = FactoryKt.makeConsentLib(spConfig2, this, MessageLanguage.ENGLISH);
        gdprConsent.setSpClient(new LocalClient());
        findViewById(R.id.review_consents).setOnClickListener(_v ->
                gdprConsent.loadPrivacyManager(
                        "13111",
                        PMTab.PURPOSES,
                        Legislation.GDPR
                ));
        findViewById(R.id.review_consents_ccpa).setOnClickListener(_v ->
                gdprConsent.loadPrivacyManager(
                        "13154",
                        PMTab.PURPOSES,
                        Legislation.CCPA
                ));
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
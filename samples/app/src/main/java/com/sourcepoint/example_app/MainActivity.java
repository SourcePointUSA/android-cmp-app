package com.sourcepoint.example_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.sourcepoint.cmplibrary.BuilderV6;
import com.sourcepoint.cmplibrary.ClientInteraction;
import com.sourcepoint.cmplibrary.gdpr.GDPRConsentLibClient;
import com.sourcepoint.example_app.core.DataProvider;
import com.sourcepoint.gdpr_cmplibrary.*;

import kotlin.Lazy;
import org.jetbrains.annotations.Nullable;

import static org.koin.java.KoinJavaComponent.inject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    final static int accountId = 22;
    final static int propertyId = 7639;
    final static String propertyName = "tcfv2.mobile.webview";
    final static String pmId = "122058";

    private ViewGroup mainViewGroup;

    private final Lazy<DataProvider> dataProvider = inject(DataProvider.class);

    private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }
    private void removeView(View view) {
        if(view.getParent() != null) mainViewGroup.removeView(view);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(accountId, propertyName, propertyId, pmId,this)
                .setOnConsentUIReady(this::showView)
                .setOnAction(actionType  -> Log.i(TAG , "ActionType: " + actionType.toString()))
                .setOnConsentUIFinished(this::removeView)
                .setOnConsentReady(consent -> {
                    // at this point it's safe to initialise vendors
                    for (String line : consent.toString().split("\n"))
                        Log.i(TAG, line);
                })
                .setAuthId(dataProvider.getValue().getAuthId())
                .setOnError(error -> Log.e(TAG, "Something went wrong"))
                .build();
    }

    // GDPRConsentLibClient
    private GDPRConsentLibClient buildGDPRConsentLibV6() {
        GDPRConsentLibClient consentLibClient =  new BuilderV6()
                .setAccountId(accountId)
                .setContext(this)
                .setPropertyName(propertyName)
                .setPropertyId(propertyId)
                .setPmId(pmId)
                .setClientInteraction(new ClientInter())
//                .setAuthId(dataProvider.getValue().getAuthId())
                .build(GDPRConsentLibClient.class);

//        consentLibClient.setClientInteraction(new ClientInter());

        return consentLibClient;
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGDPRConsentLibV6().loadMessage(dataProvider.getValue().getAuthId());
//        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v ->
                buildGDPRConsentLibV6().loadPrivacyManager(dataProvider.getValue().getAuthId())
//                buildGDPRConsentLib().showPm()
        );
        findViewById(R.id.auth_id_activity).setOnClickListener(_v -> startActivity(new Intent(this, MainActivityAuthId.class)));
    }

    class ClientInter implements ClientInteraction {

        @Override
        public void onConsentUIFinishedCallback(@Nullable View v) {
            removeView(v);
        }

        @Override
        public void onConsentUIReadyCallback(View v) {
            showView(v);
        }

        @Override
        public void onConsentReadyCallback(GDPRUserConsent consent) {
            // at this point it's safe to initialise vendors
            for (String line : consent.toString().split("\n"))
                Log.i(TAG, line);
        }

        @Override
        public void onErrorCallback(ConsentLibException v) {
            Log.e(TAG, "Something went wrong");
        }

        @Override
        public void onActionCallback(ActionTypes actionTypes) {
            Log.i(TAG , "ActionType: " + actionTypes.toString());
        }
    }
}
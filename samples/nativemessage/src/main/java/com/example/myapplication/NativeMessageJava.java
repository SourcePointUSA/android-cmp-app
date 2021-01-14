package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.NativeMessage;
import com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs;

public class NativeMessageJava extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    final static int accountId = 22;
    final static int propertyId = 7639;
    final static String propertyName = "tcfv2.mobile.webview";
    final static String pmId = "122058";

    private ViewGroup mainViewGroup;

    private void showView(View view) {
        if (view.getParent() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }

    private void removeView(View view) {
        if (view.getParent() != null) mainViewGroup.removeView(view);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(
                Accounts.nativeAccount.accountId,
                Accounts.nativeAccount.propertyName,
                Accounts.nativeAccount.propertyId,
                Accounts.nativeAccount.pmId,
                this
        )
                .setOnConsentUIReady(this::showView)
                .setOnAction(actionType -> Log.i(TAG, "ActionType: " + actionType.toString()))
                .setOnConsentUIFinished(this::removeView)
                .setOnConsentReady(consent -> {
                    // at this point it's safe to initialise vendors
                    for (String line : consent.toString().split("\n"))
                        Log.i(TAG, line);
                })
                .setOnError(error -> Log.e(TAG, "Something went wrong"))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGDPRConsentLib().run(buildNativeMessage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.consent).setOnClickListener(_v -> buildGDPRConsentLib().showPm());
    }

    private NativeMessage buildNativeMessage() {
        return new NativeMessage(this) {
            @Override
            public void init() {
                View.inflate(getContext(), R.layout.custom_layout_cl, this);
                setAcceptAll(findViewById(R.id.accept_all_cl));
                setRejectAll(findViewById(R.id.reject_all_cl));
                setShowOptions(findViewById(R.id.show_options_cl));
                setCancel(findViewById(R.id.cancel_cl));
                setTitle(findViewById(R.id.title_cl));
                setBody(findViewById(R.id.body_cl));
            }

            @Override
            public void setAttributes(NativeMessageAttrs attrs) {
                super.setAttributes(attrs);
                // This will ensure all attributes are correctly set.
                super.setAttributes(attrs);

                // Overwrite any layout after calling super.setAttributes
                getAcceptAll().button.setBackgroundColor(Color.GRAY);
                getRejectAll().button.setBackgroundColor(Color.BLUE);
            }
        };
    }
}

package com.example.authexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");

        TextView userNameTextView = findViewById(R.id.userNameTextLabel);
        userNameTextView.setText(userName);

        try {
            ConsentLib
                .newBuilder(22, "mobile.demo", this)
                .setViewGroup(findViewById(android.R.id.content))
                .setAuthId(userName)
                .setTargetingParam("MyPrivacyManager", "true")
                .setOnInteractionComplete(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        TextView consentUUIDTextView = findViewById(R.id.consentUUID);
                        consentUUIDTextView.setText(c.consentUUID);
                    }
                })
                .build()
                .run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }
}

package com.example.authexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcepoint.cmplibrary.Consent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HomeActivity extends AppCompatActivity {

    TextView userNameTextView;
    ListView consentListView;
    Toolbar toolbar;

    ConsentManager consentManager;
    ArrayAdapter<String> consentListViewAdapter;

    String userName;

    ArrayList<String> consentListViewData = loadingData();

    ArrayList<String> loadingData() {
        return new ArrayList<>(Arrays.asList("consentUUID: loading...", "euconsent: loading..."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");

        userNameTextView = findViewById(R.id.userNameTextLabel);
        consentListView = findViewById(R.id.consentListView);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        userNameTextView.setText(userName);

        consentListViewAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                consentListViewData
        );

        consentListView.setAdapter(consentListViewAdapter);

        consentManager = new ConsentManager(this) {
            @Override
            void onConsentsReady(HashSet<Consent> consents, String consentUUID, String euconsent) {
                consentListViewData.clear();
                consentListViewData.add("consentUUID: "+consentUUID);
                consentListViewData.add("euconsent: "+euconsent);
                consentListViewAdapter.notifyDataSetChanged();
            }
        };

        consentManager.loadMessage(false, userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_privacy_settings) {
            Log.d("App", "onOptionsItemSelected: " + item.getItemId());
            consentManager.loadMessage(true, userName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

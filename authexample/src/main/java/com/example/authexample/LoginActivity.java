package com.example.authexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sourcepoint.cmplibrary.Consent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText userNameInput;
    ListView consentListView;
    Toolbar toolbar;

    ArrayList<String> consentListViewData = loadingData();
    ArrayAdapter<String> consentListViewAdapter;
    ConsentManager consentManager;

    ArrayList<String> loadingData() {
        return new ArrayList<>(Arrays.asList("consentUUID: loading...", "euconsent: loading..."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        consentManager = new ConsentManager(this) {
            @Override
            void onConsentsReady(HashSet<Consent> consents, String consentUUID, String euconsent) {
                consentListViewData.clear();
                consentListViewData.add("consentUUID: "+consentUUID);
                consentListViewData.add("euconsent: "+euconsent);
                consentListViewAdapter.notifyDataSetChanged();
            }
        };

        loginButton = findViewById(R.id.button);
        userNameInput = findViewById(R.id.userNameInput);
        consentListView = findViewById(R.id.consentListView);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        userNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        consentListViewAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                consentListViewData
        );

        consentListView.setAdapter(consentListViewAdapter);

        consentManager.loadMessage(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        consentManager.loadMessage(false);
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
            consentManager.loadMessage(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLoginButtonClick(View _view) {
        login();
    }

    public void login() {
        Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
        myIntent.putExtra("userName", userNameInput.getText().toString());
        LoginActivity.this.startActivity(myIntent);
        userNameInput.setText("");
    }
}

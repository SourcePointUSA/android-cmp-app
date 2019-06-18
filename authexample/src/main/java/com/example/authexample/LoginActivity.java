package com.example.authexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.button);
        loginButton.setEnabled(false);
        EditText userName = findViewById(R.id.userNameInput);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button loginButton = findViewById(R.id.button);
                loginButton.setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    public void onLoginButtonClick(View _view) {
        login();
    }

    public void login() {
        EditText userNameInput = findViewById(R.id.userNameInput);
        String userName = userNameInput.getText().toString();
        userNameInput.setText("");

        Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
        myIntent.putExtra("userName", userName);
        LoginActivity.this.startActivity(myIntent);
    }
}

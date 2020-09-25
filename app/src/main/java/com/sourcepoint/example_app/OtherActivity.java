package com.sourcepoint.example_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class OtherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);
    }
}

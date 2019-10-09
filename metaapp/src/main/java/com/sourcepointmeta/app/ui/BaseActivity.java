package com.sourcepointmeta.app.ui;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<T extends ViewModel> extends AppCompatActivity {

    protected T viewModel;

    abstract ViewModel getViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = (T) getViewModel();
    }
}

package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ViewModelUtils {

    public static <T extends ViewModel> ViewModelProvider.Factory createFor(final T model){

        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(model.getClass())) {
                    return (T) model;
                }
                throw new IllegalArgumentException("unexpected model class modelClass");
            }
        };
    }

    private ViewModelUtils() {
        throw new IllegalStateException("Utility class");}

}

package com.sourcepointmeta.metaapp;

import androidx.multidex.MultiDexApplication;

import com.sourcepointmeta.metaapp.database.AppDataBase;
import com.sourcepointmeta.metaapp.repository.PropertyListRepository;

public class SourcepointApp extends MultiDexApplication {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    private AppDataBase getDatabase() {
        return AppDataBase.getInstance(this, mAppExecutors);
    }


    public PropertyListRepository getPropertyListRepository() {
        return PropertyListRepository.getInstance(getDatabase().propertyListDao(), mAppExecutors, getDatabase().targetingParamDao());
    }
}

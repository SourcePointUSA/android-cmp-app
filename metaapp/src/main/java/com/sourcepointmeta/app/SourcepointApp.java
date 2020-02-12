package com.sourcepointmeta.app;

import android.app.Application;

import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.repository.PropertyListRepository;

public class SourcepointApp extends Application {

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

package com.sourcepointmeta.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

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


    public WebsiteListRepository getWebsiteListRepository() {
        return WebsiteListRepository.getInstance(getDatabase().websiteListDao(), mAppExecutors, getDatabase().targetingParamDao());
    }
}

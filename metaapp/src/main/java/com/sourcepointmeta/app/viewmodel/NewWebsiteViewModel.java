package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

public class NewWebsiteViewModel extends ViewModel {

    private final WebsiteListRepository mWebsiteListRepository;
    private final String TAG = "NewWebsiteViewModel";

    public NewWebsiteViewModel(WebsiteListRepository repository) {
        mWebsiteListRepository = repository;
    }

    public LiveData<Integer> getWebsiteWithDetails(Website website){
       return mWebsiteListRepository.getWebsiteWithDetails(website);
    }
}

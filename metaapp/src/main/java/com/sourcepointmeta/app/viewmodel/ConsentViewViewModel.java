package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

public class ConsentViewViewModel extends ViewModel {

    private final WebsiteListRepository mWebsiteListRepository;
    private final String TAG = "ConsentViewViewModel";

    public ConsentViewViewModel(WebsiteListRepository repository) {
        mWebsiteListRepository = repository;
    }

    public MutableLiveData<Long> addWebsite(Website website) {
        return mWebsiteListRepository.addWebsite(website);

    }

    public MutableLiveData<Integer> updateWebsite(Website website){
        return mWebsiteListRepository.updateWebsite(website);
    }

}

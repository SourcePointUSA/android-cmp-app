package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

import java.util.List;

public class WebsiteListViewModel extends ViewModel {

    private final WebsiteListRepository mWebsiteListRepository;
    private String TAG = "WebsiteListViewModel";


    public WebsiteListViewModel(WebsiteListRepository repository) {

        mWebsiteListRepository = repository;

    }

    public LiveData<List<Website>> getWebsiteListLiveData() {
        return mWebsiteListRepository.showWebsiteList();
    }


    public MutableLiveData<Integer> deleteWebsite(Website website){
       return mWebsiteListRepository.deleteWebsite(website);
    }

}

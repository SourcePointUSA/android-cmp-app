package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.repository.PropertyListRepository;

public class ConsentViewViewModel extends ViewModel {

    private final PropertyListRepository mPropertyListRepository;
    private final String TAG = "ConsentViewViewModel";

    public ConsentViewViewModel(PropertyListRepository repository) {
        mPropertyListRepository = repository;
    }

    public MutableLiveData<Long> addProperty(Property property) {
        return mPropertyListRepository.addProperty(property);

    }

    public MutableLiveData<Integer> updateProperty(Property property){
        return mPropertyListRepository.updateProperty(property);
    }

}

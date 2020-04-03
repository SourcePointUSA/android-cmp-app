package com.sourcepointmeta.app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.repository.PropertyListRepository;

import java.util.List;

public class PropertyListViewModel extends ViewModel {

    private final PropertyListRepository mPropertyListRepository;

    public PropertyListViewModel(PropertyListRepository repository) {
        mPropertyListRepository = repository;
    }

    public LiveData<List<Property>> getPropertyListLiveData() {
        return mPropertyListRepository.showPropertyList();
    }


    public MutableLiveData<Integer> deleteProperty(Property property){
       return mPropertyListRepository.deleteProperty(property);
    }

}

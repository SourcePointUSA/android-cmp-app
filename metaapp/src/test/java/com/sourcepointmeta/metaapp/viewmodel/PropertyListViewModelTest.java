package com.sourcepointmeta.metaapp.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.metaapp.StaticTestData;
import com.sourcepointmeta.metaapp.database.AppDataBase;
import com.sourcepointmeta.metaapp.database.dao.PropertyListDao;
import com.sourcepointmeta.metaapp.database.dao.TargetingParamDao;
import com.sourcepointmeta.metaapp.database.entity.Property;
import com.sourcepointmeta.metaapp.repository.PropertyListRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PropertyListViewModelTest {

    private final PropertyListDao propertyListDao = mock(PropertyListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private PropertyListRepository propertyListRepository;
    private PropertyListViewModel viewModel ;

    @Before
    public void getViewModel(){
        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.propertyListDao()).thenReturn(propertyListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        propertyListRepository = mock(PropertyListRepository.class);
        viewModel =  new PropertyListViewModel(propertyListRepository);
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void showWebsiteList(){

        MutableLiveData<List<Property>> dbData = new MutableLiveData<>();
        List<Property> propertyList = StaticTestData.PROPERTIES;

        when(propertyListDao.getAllProperties()).thenReturn(propertyList);
        dbData.setValue(propertyList);

        doReturn(dbData).when(propertyListRepository).showPropertyList();

        Observer<List<Property>> observer = mock(Observer.class);
        viewModel.getPropertyListLiveData().observeForever(observer);
        verify(observer).onChanged(propertyList);
    }

    @Test
    public void deleteWebsiteTest(){
        MutableLiveData<Integer> websiteID = new MutableLiveData<>();
        Property property = StaticTestData.PROPERTIES.get(0);
        property.setId(1);

        when(propertyListDao.deleteProperty(property.getId())).thenReturn(1);
        websiteID.postValue(propertyListDao.deleteProperty(property.getId()));
        doReturn(websiteID).when(propertyListRepository).deleteProperty(property);


        Observer<Integer> observer = mock(Observer.class);
        viewModel.deleteProperty(property).observeForever(observer);

        verify(observer).onChanged(1);

    }

}
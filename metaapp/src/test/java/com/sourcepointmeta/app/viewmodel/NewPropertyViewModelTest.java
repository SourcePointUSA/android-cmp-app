package com.sourcepointmeta.app.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.PropertyListDao;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.models.TargetingParameterList;
import com.sourcepointmeta.app.repository.PropertyListRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewPropertyViewModelTest {

    private final PropertyListDao propertyListDao = mock(PropertyListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private PropertyListRepository propertyListRepository;
    private NewPropertyViewModel viewModel ;

    @Before
    public void getViewModel(){

        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.propertyListDao()).thenReturn(propertyListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        propertyListRepository = mock(PropertyListRepository.class);

        viewModel =  new NewPropertyViewModel(propertyListRepository);

    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testGetPropertyWithDetailsWithParameters() {

        MutableLiveData<Integer> size = new MutableLiveData<>();

        Property property = StaticTestData.PROPERTIES.get(0);
        Observer<Integer> observer = mock(Observer.class);

        List<TargetingParam> list = property.getTargetingParamList();
        Collections.sort(list, new Comparator<TargetingParam>() {
            public int compare(TargetingParam v1, TargetingParam v2) {
                return v1.getKey().compareTo(v2.getKey());
            }
        });

        StringBuilder keyListBuilder = new StringBuilder();
        StringBuilder valueListBuilder = new StringBuilder();

        for (TargetingParam targetingParam : list) {
            keyListBuilder = keyListBuilder.append(targetingParam.getKey());
            keyListBuilder.append(",");
            valueListBuilder.append(targetingParam.getValue());
            valueListBuilder.append(",");
        }

        String keyList = "";
        if (!keyListBuilder.toString().isEmpty()) {
            keyList = keyListBuilder.toString().substring(0, keyListBuilder.length() - 1);
        }

        String valueList = "";
        if (!valueListBuilder.toString().isEmpty()) {
            valueList = valueListBuilder.toString().substring(0, valueListBuilder.length() - 1);
        }

        if (keyList.isEmpty()){
            when(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID() , property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId())).thenReturn(1);
            size.postValue(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID() , property.getProperty(), property.getPmID() , property.isStaging(), property.isShowPM(), property.getAuthId()));
        }else {
            TargetingParameterList targetingParameterList = new TargetingParameterList();
            targetingParameterList.setKeyList(keyList);
            targetingParameterList.setValueList(valueList);
            List<TargetingParameterList> targetingParameterLists = Arrays.asList(targetingParameterList);
            when(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(),
                    keyList, valueList)).thenReturn(targetingParameterLists);
            size.postValue(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(),
                    keyList, valueList).size());

        }
        when(propertyListRepository.getPropertyWithDetails(property)).thenReturn(size);

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) viewModel.getPropertyWithDetails(property);
        listSize.observeForever(observer);

        int count = listSize.getValue();

        verify(observer).onChanged(1);
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }

    @Test
    public void testGetPropertyWithDetails() {

        MutableLiveData<Integer> size = new MutableLiveData<>();

        Property property = StaticTestData.PROPERTIES.get(2);
        Observer<Integer> observer = mock(Observer.class);
        doReturn(1).when(propertyListDao).getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isStaging(), property.getAuthId());
        size.postValue(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId()));
        when(propertyListRepository.getPropertyWithDetails(property)).thenReturn(size);

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) viewModel.getPropertyWithDetails(property);
        listSize.observeForever(observer);
        int count = listSize.getValue();
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }
}
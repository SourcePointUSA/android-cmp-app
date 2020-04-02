package com.sourcepointmeta.app.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.PropertyListDao;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.models.TargetingParameterList;
import com.sourcepointmeta.app.utils.InstantAppExecutors;

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

public class PropertyListRepositoryTest {

    private final PropertyListDao propertyListDao = mock(PropertyListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private PropertyListRepository propertyListRepository;

    @Before
    public void getRepositoryInstance() {
        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.propertyListDao()).thenReturn(propertyListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        propertyListRepository = new PropertyListRepository( new InstantAppExecutors(),appDataBase.propertyListDao(), appDataBase.targetingParamDao());
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void showPropertyListTest() {

        List<Property> propertyList = StaticTestData.PROPERTIES;

        when(propertyListDao.getAllProperties()).thenReturn(propertyList);

        Observer<List<Property>> observer = mock(Observer.class);

        LiveData<List<Property>> propertyListLiveData = propertyListRepository.showPropertyList();

        propertyListLiveData.observeForever(observer);
        verify(observer).onChanged(propertyList);
    }

    @Test
    public void getAllPropertiesTest(){
        List<Property> propertyList = StaticTestData.PROPERTIES;

        when(propertyListDao.getAllProperties()).thenReturn(propertyList);
        List<Property> properties = propertyListRepository.getAllProperties();

        assertEquals(propertyList, properties);

    }

    @Test
    public void addPropertyTest() {
        Property property = StaticTestData.PROPERTIES.get(0);
        propertyListDao.insert(property);
        verify(propertyListDao).insert(property);

        when(propertyListDao.insert(property)).thenReturn((long) 1);

        assertEquals(1, propertyListDao.insert(property));
    }

    @Test
    public void updatePropertyTest() {
        Property property = StaticTestData.PROPERTIES.get(0);
        property.setId(1);

        propertyListDao.update(property.getAccountID(), property.getPropertyID() , property.getProperty(), property.getPmID() , property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId());
        verify(propertyListDao).update(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId());

        when(propertyListDao.update(property.getAccountID(), property.getPropertyID() , property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId())).thenReturn(1);
        assertEquals(1, propertyListDao.update(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId()));
    }

    @Test
    public void testGetPropertyWithDetailsWithParameters() {

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
            when(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID() , property.isStaging(), property.isShowPM() , property.getAuthId())).thenReturn(1);
        }else {
            TargetingParameterList targetingParameterList = new TargetingParameterList();
            targetingParameterList.setKeyList(keyList);
            targetingParameterList.setValueList(valueList);
            List<TargetingParameterList> targetingParameterLists = Arrays.asList(targetingParameterList);
            when(propertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM() , property.getAuthId(),
                    keyList, valueList)).thenReturn(targetingParameterLists);
        }

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) propertyListRepository.getPropertyWithDetails(property);
        listSize.observeForever(observer);

        int count = listSize.getValue();

       verify(observer).onChanged(1);
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }

    @Test
    public void testGetPropertyWithDetails() {

        Property property = StaticTestData.PROPERTIES.get(2);
        Observer<Integer> observer = mock(Observer.class);
        doReturn(1).when(propertyListDao).getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID() , property.isStaging(), property.isShowPM(), property.getAuthId());


        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) propertyListRepository.getPropertyWithDetails(property);
        listSize.observeForever(observer);
        int count = listSize.getValue();
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }


    @Test
    public void testDeleteProperty(){
        MutableLiveData<List<Property>> dbData = new MutableLiveData<>();
        dbData.setValue(StaticTestData.PROPERTIES);
        doReturn(StaticTestData.PROPERTIES).when(propertyListDao).getAllProperties();
        List<Property> properties = propertyListDao.getAllProperties();
        properties.get(0).setId(1);
        when(propertyListDao.deleteProperty(properties.get(0).getId())).thenReturn(1);
        assertEquals(1, propertyListDao.deleteProperty(1));
    }

}

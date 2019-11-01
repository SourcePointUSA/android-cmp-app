package com.sourcepointmeta.app.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.models.TargetingParameterList;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

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

public class NewWebsiteViewModelTest {

    private final WebsiteListDao websiteListDao = mock(WebsiteListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private  WebsiteListRepository websiteListRepository;
    private NewWebsiteViewModel viewModel ;

    @Before
    public void getViewModel(){

        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.websiteListDao()).thenReturn(websiteListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        websiteListRepository = mock(WebsiteListRepository.class);

        viewModel =  new NewWebsiteViewModel(websiteListRepository);

    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testGetWebsiteWithDetailsWithParameters() {

        MutableLiveData<Integer> size = new MutableLiveData<>();

        Website website = StaticTestData.WEBSITES.get(0);
        Observer<Integer> observer = mock(Observer.class);

        List<TargetingParam> list = website.getTargetingParamList();
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
            when(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID() ,website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(), website.getAuthId())).thenReturn(1);
            size.postValue(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID() ,website.getName(), website.getPmID() ,website.isStaging(), website.isShowPM(),website.getAuthId()));
        }else {
            TargetingParameterList targetingParameterList = new TargetingParameterList();
            targetingParameterList.setKeyList(keyList);
            targetingParameterList.setValueList(valueList);
            List<TargetingParameterList> targetingParameterLists = Arrays.asList(targetingParameterList);
            when(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(),website.isStaging(), website.isShowPM(), website.getAuthId(),
                    keyList, valueList)).thenReturn(targetingParameterLists);
            size.postValue(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(), website.getAuthId(),
                    keyList, valueList).size());

        }
        when(websiteListRepository.getWebsiteWithDetails(website)).thenReturn(size);

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) viewModel.getWebsiteWithDetails(website);
        listSize.observeForever(observer);

        int count = listSize.getValue();

        verify(observer).onChanged(1);
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }

    @Test
    public void testGetWebsiteWithDetails() {

        MutableLiveData<Integer> size = new MutableLiveData<>();

        Website website = StaticTestData.WEBSITES.get(2);
        Observer<Integer> observer = mock(Observer.class);
        doReturn(1).when(websiteListDao).getWebsiteWithDetails(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(), website.isStaging(), website.getAuthId());
        size.postValue(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID(),website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(), website.getAuthId()));
        when(websiteListRepository.getWebsiteWithDetails(website)).thenReturn(size);

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) viewModel.getWebsiteWithDetails(website);
        listSize.observeForever(observer);
        int count = listSize.getValue();
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }
}
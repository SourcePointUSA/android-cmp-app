package com.sourcepointmeta.app.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.repository.WebsiteListRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebsiteListViewModelTest {

    private final WebsiteListDao websiteListDao = mock(WebsiteListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private  WebsiteListRepository websiteListRepository;
    private WebsiteListViewModel viewModel ;

    @Before
    public void getViewModel(){
        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.websiteListDao()).thenReturn(websiteListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        websiteListRepository = mock(WebsiteListRepository.class);
        viewModel =  new WebsiteListViewModel(websiteListRepository);
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void showWebsiteList(){

        MutableLiveData<List<Website>> dbData = new MutableLiveData<>();
        List<Website> websiteList = StaticTestData.WEBSITES;

        when(websiteListDao.getAllSites()).thenReturn(websiteList);
        dbData.setValue(websiteList);

        doReturn(dbData).when(websiteListRepository).showWebsiteList();

        Observer<List<Website>> observer = mock(Observer.class);
        viewModel.getWebsiteListLiveData().observeForever(observer);
        verify(observer).onChanged(websiteList);
    }

    @Test
    public void deleteWebsiteTest(){
        MutableLiveData<Integer> websiteID = new MutableLiveData<>();
        Website website = StaticTestData.WEBSITES.get(0);
        website.setId(1);

        when(websiteListDao.deleteWebsite(website.getId())).thenReturn(1);
        websiteID.postValue(websiteListDao.deleteWebsite(website.getId()));
        doReturn(websiteID).when(websiteListRepository).deleteWebsite(website);


        Observer<Integer> observer = mock(Observer.class);
        viewModel.deleteWebsite(website).observeForever(observer);

        verify(observer).onChanged(1);

    }

}
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsentViewViewModelTest {

    private final WebsiteListDao websiteListDao = mock(WebsiteListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private WebsiteListRepository websiteListRepository;
    private ConsentViewViewModel viewModel ;

    @Before
    public void getViewModel(){

        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.websiteListDao()).thenReturn(websiteListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        websiteListRepository = mock(WebsiteListRepository.class);

        viewModel =  new ConsentViewViewModel(websiteListRepository);

    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void addWebsite() {
        MutableLiveData<Long> websiteID = new MutableLiveData<>();

        Website website = StaticTestData.WEBSITES.get(0);
        website.setId(1);
        long id = 1;

        when(websiteListDao.insert(website)).thenReturn(id);
        websiteID.postValue(websiteListDao.insert(website));
        doReturn(websiteID).when(websiteListRepository).addWebsite(website);

        Observer<Long> observer = mock(Observer.class);
        viewModel.addWebsite(website).observeForever(observer);

        verify(observer).onChanged(id);
    }

    @Test
    public void updateWebsite() {

        MutableLiveData<Integer> websiteID = new MutableLiveData<>();
        Website website = StaticTestData.WEBSITES.get(0);
        website.setId(1);

        when(websiteListDao.update(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(), website.getAuthId(), website.getId())).thenReturn(1);
        websiteID.postValue(websiteListDao.update(website.getAccountID() , website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(), website.getAuthId(), website.getId()));
        doReturn(websiteID).when(websiteListRepository).updateWebsite(website);


        Observer<Integer> observer = mock(Observer.class);
        viewModel.updateWebsite(website).observeForever(observer);

        verify(observer).onChanged(1);
    }
}
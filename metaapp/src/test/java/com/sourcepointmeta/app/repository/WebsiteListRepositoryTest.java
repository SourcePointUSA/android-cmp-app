package com.sourcepointmeta.app.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
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

public class WebsiteListRepositoryTest {

    private final WebsiteListDao websiteListDao = mock(WebsiteListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private WebsiteListRepository websiteListRepository;

    @Before
    public void getRepositoryInstance() {
        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.websiteListDao()).thenReturn(websiteListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        websiteListRepository = new WebsiteListRepository( new InstantAppExecutors(),appDataBase.websiteListDao(), appDataBase.targetingParamDao());
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void showWebsiteListTest() {

        List<Website> websiteList = StaticTestData.WEBSITES;

        when(websiteListDao.getAllSites()).thenReturn(websiteList);

        Observer<List<Website>> observer = mock(Observer.class);

        LiveData<List<Website>> websiteLiveData = websiteListRepository.showWebsiteList();

        websiteLiveData.observeForever(observer);
        verify(observer).onChanged(websiteList);
    }

    @Test
    public void getAllSitesTest(){
        List<Website> websiteList = StaticTestData.WEBSITES;

        when(websiteListDao.getAllSites()).thenReturn(websiteList);
        List<Website> websites = websiteListRepository.getAllSites();

        assertEquals(websiteList,websites);

    }

    @Test
    public void addWebsiteTest() {
        Website website = StaticTestData.WEBSITES.get(0);
        websiteListDao.insert(website);
        verify(websiteListDao).insert(website);

        when(websiteListDao.insert(website)).thenReturn((long) 1);

        assertEquals(1, websiteListDao.insert(website));
    }

    @Test
    public void updateWebsiteTest() {
        Website website = StaticTestData.WEBSITES.get(0);
        website.setId(1);

        websiteListDao.update(website.getAccountID(),website.getSiteID() ,website.getName(), website.getPmID() ,website.isStaging(), website.isShowPM(),website.getAuthId(), website.getId());
        verify(websiteListDao).update(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(),website.isShowPM(), website.getAuthId(), website.getId());

        when(websiteListDao.update(website.getAccountID(),website.getSiteID() ,website.getName(), website.getPmID(),website.isStaging(), website.isShowPM(),website.getAuthId(), website.getId())).thenReturn(1);
        assertEquals(1, websiteListDao.update(website.getAccountID(), website.getSiteID(), website.getName(), website.getPmID(), website.isStaging(), website.isShowPM(),website.getAuthId(), website.getId()));
    }

    @Test
    public void testGetWebsiteWithDetailsWithParameters() {

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
            when(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID(),website.getName(), website.getPmID() ,website.isStaging(), website.isShowPM() ,website.getAuthId())).thenReturn(1);
        }else {
            TargetingParameterList targetingParameterList = new TargetingParameterList();
            targetingParameterList.setKeyList(keyList);
            targetingParameterList.setValueList(valueList);
            List<TargetingParameterList> targetingParameterLists = Arrays.asList(targetingParameterList);
            when(websiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getSiteID(),website.getName(), website.getPmID(),website.isStaging(), website.isShowPM() ,website.getAuthId(),
                    keyList, valueList)).thenReturn(targetingParameterLists);
        }

        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) websiteListRepository.getWebsiteWithDetails(website);
        listSize.observeForever(observer);

        int count = listSize.getValue();

       verify(observer).onChanged(1);
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }

    @Test
    public void testGetWebsiteWithDetails() {

        Website website = StaticTestData.WEBSITES.get(2);
        Observer<Integer> observer = mock(Observer.class);
        doReturn(1).when(websiteListDao).getWebsiteWithDetails(website.getAccountID(), website.getSiteID(),website.getName(), website.getPmID() ,website.isStaging(), website.isShowPM(),website.getAuthId());


        MutableLiveData<Integer> listSize = (MutableLiveData<Integer>) websiteListRepository.getWebsiteWithDetails(website);
        listSize.observeForever(observer);
        int count = listSize.getValue();
        assertEquals(1,count);
        listSize.removeObserver(observer);

    }


    @Test
    public void testDeleteWebsite(){
        MutableLiveData<List<Website>> dbData = new MutableLiveData<>();
        dbData.setValue(StaticTestData.WEBSITES);
        doReturn(StaticTestData.WEBSITES).when(websiteListDao).getAllSites();
        List<Website> websites = websiteListDao.getAllSites();
        websites.get(0).setId(1);
        when(websiteListDao.deleteWebsite(websites.get(0).getId())).thenReturn(1);
        assertEquals(1, websiteListDao.deleteWebsite(1));
    }

}

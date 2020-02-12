package com.sourcepointmeta.app.repository;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.sourcepointmeta.app.AppExecutors;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.PropertyListDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PropertyListRepositoryTest {

    private PropertyListDao propertyListDao;
    private TargetingParamDao targetingParamDao;
    private AppDataBase appDataBase;
    private AppExecutors mAppExecutors;
    private PropertyListRepository mPropertyListRepository;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getContext();
        appDataBase = Room.inMemoryDatabaseBuilder(context, AppDataBase.class).build();
        propertyListDao = appDataBase.propertyListDao();
        targetingParamDao = appDataBase.targetingParamDao();


        mAppExecutors = new AppExecutors();
        mPropertyListRepository = PropertyListRepository.getInstance(propertyListDao, mAppExecutors,targetingParamDao);
    }

    @After
    public void closeDb() throws IOException {
        appDataBase.close();
    }


    @Before
    public void getRepositoryInstance() {

    }

    @Test
    public void getInstance(){
        PropertyListRepository propertyListRepository = PropertyListRepository.getInstance(propertyListDao, mAppExecutors,targetingParamDao);

        assertEquals(propertyListRepository, mPropertyListRepository);
    }

/*
    @Test
    public void showWebsiteList() {
        List<Property> websites = TestData.PROPERTIES;
        propertyListDao.insert(websites.get(0));
        propertyListDao.insert(websites.get(1));
        List<Property> list = mPropertyListRepository.getAllProperties();
        assertEquals(list.size(),websites.size());
    }

    @Test
    public void addWebsite() {
        List<Property> websites = TestData.PROPERTIES;
        Property website = websites.get(0);
        mPropertyListRepository.addProperty(website);

        assertEquals(website.getAccountID(),propertyListDao.getPropertyWithDetails(website.getAccountID(),
                website.getProperty(), website.isStaging()).get(0).getAccountID());

        assertEquals(website.getProperty(),propertyListDao.getPropertyWithDetails(website.getAccountID(),
                website.getProperty(), website.isStaging()).get(0).getProperty());

        assertEquals(website.isStaging(),propertyListDao.getPropertyWithDetails(website.getAccountID(),
                website.getProperty(), website.isStaging()).get(0).isStaging());

    }

    @Test
    public void updateWebsite() {

        List<Property> websites = TestData.PROPERTIES;
        Property initialWebsite = websites.get(0);
        propertyListDao.insert(initialWebsite);
        Property updatedSite = websites.get(1);
        updatedSite.setId(1);
        mPropertyListRepository.updateProperty(updatedSite);

        assertEquals(updatedSite.getAccountID(),propertyListDao.getPropertyWithDetails(updatedSite.getAccountID(),
                updatedSite.getProperty(), updatedSite.isStaging()).get(0).getAccountID());

        assertEquals(updatedSite.getProperty(),propertyListDao.getPropertyWithDetails(updatedSite.getAccountID(),
                updatedSite.getProperty(), updatedSite.isStaging()).get(0).getProperty());

        assertEquals(updatedSite.isStaging(),propertyListDao.getPropertyWithDetails(updatedSite.getAccountID(),
                updatedSite.getProperty(), updatedSite.isStaging()).get(0).isStaging());
    }

    @Test
    public void getWebsiteWithDetails() {
        List<Property> websites = TestData.PROPERTIES;
        Property initialWebsite = websites.get(0);
        propertyListDao.insert(initialWebsite);

        assertEquals(initialWebsite.getAccountID(),propertyListDao.getPropertyWithDetails(initialWebsite.getAccountID(),initialWebsite.getPropertyID(),
                initialWebsite.getProperty(), initialWebsite.getPmID(),initialWebsite.isStaging(),initialWebsite.isShowPM(),initialWebsite.getAuthId()));
    }*/

}
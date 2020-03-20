//package com.sourcepointmeta.app.repository;
//
//import android.arch.persistence.room.Room;
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//
//import com.sourcepointmeta.app.AppExecutors;
//import com.sourcepointmeta.app.TestData;
//import com.sourcepointmeta.app.database.AppDataBase;
//import com.sourcepointmeta.app.database.dao.WebsiteListDao;
//import com.sourcepointmeta.app.database.entity.Website;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//public class WebsiteListRepositoryTest {
//
//    private WebsiteListDao websiteListDao;
//    private AppDataBase appDataBase;
//    private AppExecutors mAppExecutors;
//    private WebsiteListRepository mWebsiteListRepository;
//
//    @Before
//    public void createDb() {
//        Context context = InstrumentationRegistry.getContext();
//        appDataBase = Room.inMemoryDatabaseBuilder(context, AppDataBase.class).build();
//        websiteListDao = appDataBase.websiteListDao();
//
//        mAppExecutors = new AppExecutors();
//        mWebsiteListRepository = WebsiteListRepository.getInstance(websiteListDao, mAppExecutors);
//    }
//
//    @After
//    public void closeDb() throws IOException {
//        appDataBase.close();
//    }
//
//
//    @Before
//    public void getRepositoryInstance() {
//
//    }
//
//    @Test
//    public void getInstance(){
//        WebsiteListRepository websiteListRepository = WebsiteListRepository.getInstance(websiteListDao , mAppExecutors);
//
//        assertEquals(websiteListRepository , mWebsiteListRepository);
//    }
//
//
//    @Test
//    public void showWebsiteList() {
//        List<Website> websites = TestData.WEBSITES;
//        websiteListDao.insert(websites.get(0));
//        websiteListDao.insert(websites.get(1));
//        List<Website> list = mWebsiteListRepository.getAllSites();
//        assertEquals(list.size(),websites.size());
//    }
//
//    @Test
//    public void addWebsite() {
//        List<Website> websites = TestData.WEBSITES;
//        Website website = websites.get(0);
//        mWebsiteListRepository.addWebsite(website);
//
//        assertEquals(website.getAccountID(),websiteListDao.getWebsiteWithDetails(website.getAccountID(),
//                website.getName(), website.isStaging()).get(0).getAccountID());
//
//        assertEquals(website.getName(),websiteListDao.getWebsiteWithDetails(website.getAccountID(),
//                website.getName(), website.isStaging()).get(0).getName());
//
//        assertEquals(website.isStaging(),websiteListDao.getWebsiteWithDetails(website.getAccountID(),
//                website.getName(), website.isStaging()).get(0).isStaging());
//
//    }
//
//    @Test
//    public void updateWebsite() {
//
//        List<Website> websites = TestData.WEBSITES;
//        Website initialWebsite = websites.get(0);
//        websiteListDao.insert(initialWebsite);
//        Website updatedSite = websites.get(1);
//        updatedSite.setId(1);
//        mWebsiteListRepository.updateWebsite(updatedSite);
//
//        assertEquals(updatedSite.getAccountID(),websiteListDao.getWebsiteWithDetails(updatedSite.getAccountID(),
//                updatedSite.getName(), updatedSite.isStaging()).get(0).getAccountID());
//
//        assertEquals(updatedSite.getName(),websiteListDao.getWebsiteWithDetails(updatedSite.getAccountID(),
//                updatedSite.getName(), updatedSite.isStaging()).get(0).getName());
//
//        assertEquals(updatedSite.isStaging(),websiteListDao.getWebsiteWithDetails(updatedSite.getAccountID(),
//                updatedSite.getName(), updatedSite.isStaging()).get(0).isStaging());
//    }
//
//    @Test
//    public void getWebsiteWithDetails() {
//        List<Website> websites = TestData.WEBSITES;
//        Website initialWebsite = websites.get(0);
//        websiteListDao.insert(initialWebsite);
//
//        assertEquals(initialWebsite.getAccountID(),websiteListDao.getWebsiteWithDetails(initialWebsite.getAccountID(),
//                initialWebsite.getName(), initialWebsite.isStaging()).get(0).getAccountID());
//    }
//
//}
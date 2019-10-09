package com.sourcepointmeta.app.database;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sourcepointmeta.app.LiveDataTestUtil;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.Website;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.sourcepointmeta.app.TestData.WEBSITES;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


import java.util.List;

@RunWith(AndroidJUnit4.class)
public class WebsiteListDaoTest {

    private AppDataBase mDatabase;
    private WebsiteListDao mWebsiteListDao;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDataBase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
        mWebsiteListDao = mDatabase.websiteListDao();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void getWebsitesWhenNoWebsiteInserted() throws InterruptedException {
        List<Website> websiteList = LiveDataTestUtil.getValue(mWebsiteListDao.getAllWebsites());
        assertTrue(websiteList.isEmpty());
    }

    @Test
    public void getProductsAfterInserted() throws InterruptedException {
        mWebsiteListDao.insert(WEBSITES.get(0));
        mWebsiteListDao.insert(WEBSITES.get(1));

        List<Website> websites = LiveDataTestUtil.getValue(mWebsiteListDao.getAllWebsites());
        assertThat(websites.size(), is(WEBSITES.size()));
    }

    @Test
    public void getUpdatedWebsiteID() throws InterruptedException {
        mWebsiteListDao.insert(WEBSITES.get(0));
        mWebsiteListDao.insert(WEBSITES.get(1));

        List<Website> websites = LiveDataTestUtil.getValue(mWebsiteListDao.getAllWebsites());
        Website website = LiveDataTestUtil.getValue(mWebsiteListDao.getWebsiteByID(websites.get(1).getId()));

        assertThat(website.getId(), is(websites.get(1).getId()));
        assertThat(website.getName(), is(websites.get(1).getName()));
        assertThat(website.getAccountID(), is(websites.get(1).getAccountID()));
        assertThat(website.isStaging(), is(websites.get(1).isStaging()));
    }
}

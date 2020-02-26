package com.sourcepointmeta.app.database;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sourcepointmeta.app.LiveDataTestUtil;
import com.sourcepointmeta.app.database.dao.PropertyListDao;
import com.sourcepointmeta.app.database.entity.Property;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.sourcepointmeta.app.TestData.PROPERTIES;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PropertyListDaoTest {

    private AppDataBase mDatabase;
    private PropertyListDao mPropertyListDao;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDataBase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
        mPropertyListDao = mDatabase.propertyListDao();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void getWebsitesWhenNoWebsiteInserted() throws InterruptedException {
        List<Property> propertyList = LiveDataTestUtil.getValue(mPropertyListDao.getProperties());
        assertTrue(propertyList.isEmpty());
    }

    @Test
    public void getProductsAfterInserted() throws InterruptedException {
        mPropertyListDao.insert(PROPERTIES.get(0));
        mPropertyListDao.insert(PROPERTIES.get(1));

        List<Property> properties = LiveDataTestUtil.getValue(mPropertyListDao.getProperties());
        assertThat(properties.size(), is(PROPERTIES.size()));
    }

    @Test
    public void getUpdatedWebsiteID() throws InterruptedException {
        mPropertyListDao.insert(PROPERTIES.get(0));
        mPropertyListDao.insert(PROPERTIES.get(1));

        List<Property> properties = LiveDataTestUtil.getValue(mPropertyListDao.getProperties());
        Property property = LiveDataTestUtil.getValue(mPropertyListDao.getPropertyByID(properties.get(1).getId()));

        assertThat(property.getId(), is(properties.get(1).getId()));
        assertThat(property.getProperty(), is(properties.get(1).getProperty()));
        assertThat(property.getAccountID(), is(properties.get(1).getAccountID()));
        assertThat(property.isStaging(), is(properties.get(1).isStaging()));
    }
}

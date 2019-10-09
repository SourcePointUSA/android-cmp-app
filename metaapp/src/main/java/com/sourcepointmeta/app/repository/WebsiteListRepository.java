package com.sourcepointmeta.app.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.sourcepointmeta.app.AppExecutors;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.models.TargetingParameterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WebsiteListRepository {

    private static final String TAG = "WebsiteListRepository";
    private static WebsiteListRepository sInstance;
    private final WebsiteListDao mWebsiteListDao;
    private final TargetingParamDao mTargetingParamDao;
    private final AppExecutors mAppExecutors;
    private MutableLiveData<List<Website>> mWebListLiveData = new MutableLiveData<>();


    // constructor
    private WebsiteListRepository(final WebsiteListDao dao, final AppExecutors appExecutors, final TargetingParamDao targetingParamDao) {
        mWebsiteListDao = dao;
        mTargetingParamDao = targetingParamDao;
        mAppExecutors = appExecutors;
    }

    @VisibleForTesting
    public WebsiteListRepository(  AppExecutors appExecutors, WebsiteListDao dao,  TargetingParamDao targetingParamDao){
        mWebsiteListDao = dao;
        mTargetingParamDao = targetingParamDao;
        mAppExecutors = appExecutors;
    }

    // method to get singleton instance on repository class
    public static synchronized WebsiteListRepository getInstance(WebsiteListDao websiteListDao, AppExecutors appExecutors, TargetingParamDao targetingParamDao) {

        if (sInstance == null) {
            sInstance = new WebsiteListRepository(websiteListDao, appExecutors, targetingParamDao);
        }
        return sInstance;
    }

    //method get all websites data from database
    public LiveData<List<Website>> showWebsiteList() {
        mAppExecutors.diskIO().execute(() -> {
            List<Website> websites = mWebsiteListDao.getAllSites();
            for (Website website : websites) {
                website.setTargetingParamList(mTargetingParamDao.getAllTargetingParam(website.getId()));
            }
            mWebListLiveData.postValue(websites);
        });
        return mWebListLiveData;
    }

    public List<Website> getAllSites() {
        return mWebsiteListDao.getAllSites();
    }

    // method to add website details to data base
    public MutableLiveData<Long> addWebsite(final Website website) {
        MutableLiveData<Long> websiteID = new MutableLiveData<>();
        mAppExecutors.diskIO().execute(() -> {

            try {
                long websiteId = mWebsiteListDao.insert(website);
                websiteID.postValue(websiteId);

                List<TargetingParam> listOfParams = new ArrayList<>();
                List<TargetingParam> targetingParamList = website.getTargetingParamList();

                for (TargetingParam targetingParam : targetingParamList) {
                    targetingParam.setRefID(websiteId);
                    listOfParams.add(targetingParam);
                }
                long[] id = mTargetingParamDao.insert(listOfParams);
                Log.d(TAG, "id of websites added : " + websiteId);
                Log.d(TAG, "no of params added : " + id.length);

            } catch (SQLiteConstraintException exception) {
                Log.d(TAG, "website already added to database");
            } catch (Exception exception) {
                Log.d(TAG, "website not added to database");
            }
        });
        return websiteID;
    }


    // method to update the existing data of website use website.getID for update call
    public MutableLiveData<Integer> updateWebsite(final Website website) {
        MutableLiveData<Integer> websiteID = new MutableLiveData<>();
        mAppExecutors.diskIO().execute(() -> {
            Log.d(TAG, website.getName() + " " + website.getAccountID() + " " + website.isStaging() + " " + website.getId());
            int websiteId = mWebsiteListDao.update(website.getAccountID(), website.getName(), website.isStaging(), website.getAuthId(),website.getId());
            websiteID.postValue(websiteId);
            List<TargetingParam> listOfParams = new ArrayList<>();
            List<TargetingParam> targetingParamList = website.getTargetingParamList();
            List<TargetingParam> oldList = mTargetingParamDao.getAllTargetingParam(website.getId());


            for (TargetingParam targetingParam : targetingParamList) {
                targetingParam.setRefID(website.getId());
                listOfParams.add(targetingParam);
            }

            for (TargetingParam targetingParam : listOfParams) {
                if (oldList.contains(targetingParam)) {
                    mTargetingParamDao.updateParameter(targetingParam.getKey(), targetingParam.getValue(), targetingParam.getRefID());
                } else {
                    mTargetingParamDao.insertParameter(targetingParam);
                }
            }

            for (TargetingParam targetingParam : oldList) {
                if (!listOfParams.contains(targetingParam)) {
                    mTargetingParamDao.deleteParameter(targetingParam.getId());
                }
            }

        });
        return websiteID;
    }

    public LiveData<Integer> getWebsiteWithDetails(Website website) {
        MutableLiveData<Integer> listSize = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {

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
                int siteCount = mWebsiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getName(), website.isStaging(), website.getAuthId());
                listSize.postValue(siteCount);
            }else {

            List<TargetingParameterList> paramList = mWebsiteListDao.getWebsiteWithDetails(website.getAccountID(), website.getName(), website.isStaging(),
                    website.getAuthId() ,keyList, valueList);
            listSize.postValue(paramList.size());
            }
            //check if sites exists
        });
        return listSize;
    }

    public MutableLiveData<Integer> deleteWebsite(Website website) {
        MutableLiveData<Integer> websiteID = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            mTargetingParamDao.deleteAll(website.getId());
           int websiteId =  mWebsiteListDao.deleteWebsite(website.getId());
           websiteID.postValue(websiteId);

        });
        return websiteID;
    }
}

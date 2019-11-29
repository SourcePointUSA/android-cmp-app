package com.sourcepointmeta.app.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.sourcepointmeta.app.AppExecutors;
import com.sourcepointmeta.app.database.dao.PropertyListDao;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.models.TargetingParameterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PropertyListRepository {

    private static final String TAG = "PropertyListRepository";
    private static PropertyListRepository sInstance;
    private final PropertyListDao mPropertyListDao;
    private final TargetingParamDao mTargetingParamDao;
    private final AppExecutors mAppExecutors;
    private MutableLiveData<List<Property>> mWebListLiveData = new MutableLiveData<>();


    // constructor
    private PropertyListRepository(final PropertyListDao dao, final AppExecutors appExecutors, final TargetingParamDao targetingParamDao) {
        mPropertyListDao = dao;
        mTargetingParamDao = targetingParamDao;
        mAppExecutors = appExecutors;
    }

    @VisibleForTesting
    public PropertyListRepository(AppExecutors appExecutors, PropertyListDao dao, TargetingParamDao targetingParamDao){
        mPropertyListDao = dao;
        mTargetingParamDao = targetingParamDao;
        mAppExecutors = appExecutors;
    }

    // method to get singleton instance on repository class
    public static synchronized PropertyListRepository getInstance(PropertyListDao propertyListDao, AppExecutors appExecutors, TargetingParamDao targetingParamDao) {

        if (sInstance == null) {
            sInstance = new PropertyListRepository(propertyListDao, appExecutors, targetingParamDao);
        }
        return sInstance;
    }

    //method get all property data from database
    public LiveData<List<Property>> showPropertyList() {
        mAppExecutors.diskIO().execute(() -> {
            List<Property> properties = mPropertyListDao.getAllProperties();
            for (Property proeprty : properties) {
                proeprty.setTargetingParamList(mTargetingParamDao.getAllTargetingParam(proeprty.getId()));
            }
            mWebListLiveData.postValue(properties);
        });
        return mWebListLiveData;
    }

    public List<Property> getAllProperties() {
        return mPropertyListDao.getAllProperties();
    }

    // method to add property details to data base
    public MutableLiveData<Long> addProperty(final Property property) {
        MutableLiveData<Long> propertyID = new MutableLiveData<>();
        mAppExecutors.diskIO().execute(() -> {

            try {
                long propertyId = mPropertyListDao.insert(property);
                propertyID.postValue(propertyId);

                List<TargetingParam> listOfParams = new ArrayList<>();
                List<TargetingParam> targetingParamList = property.getTargetingParamList();

                for (TargetingParam targetingParam : targetingParamList) {
                    targetingParam.setRefID(propertyId);
                    listOfParams.add(targetingParam);
                }
                long[] id = mTargetingParamDao.insert(listOfParams);
                Log.d(TAG, "id of properties added : " + propertyId);
                Log.d(TAG, "no of params added : " + id.length);

            } catch (SQLiteConstraintException exception) {
                Log.d(TAG, "property already added to database");
            } catch (Exception exception) {
                Log.d(TAG, "property not added to database");
            }
        });
        return propertyID;
    }


    // method to update the existing data of property use property.getID for update call
    public MutableLiveData<Integer> updateProperty(final Property property) {
        MutableLiveData<Integer> propertyID = new MutableLiveData<>();
        mAppExecutors.diskIO().execute(() -> {
            Log.d(TAG, property.getProperty() + " " + property.getAccountID() + " " + property.isStaging() + " " + property.getId());
            int propertyId = mPropertyListDao.update(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID() ,property.isStaging(), property.isShowPM(), property.getAuthId(),property.getId());
            propertyID.postValue(propertyId);
            List<TargetingParam> listOfParams = new ArrayList<>();
            List<TargetingParam> targetingParamList = property.getTargetingParamList();
            List<TargetingParam> oldList = mTargetingParamDao.getAllTargetingParam(property.getId());


            for (TargetingParam targetingParam : targetingParamList) {
                targetingParam.setRefID(property.getId());
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
        return propertyID;
    }

    public LiveData<Integer> getPropertyWithDetails(Property property) {
        MutableLiveData<Integer> listSize = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {

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
                int propertyCount = mPropertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM() ,property.getAuthId());
                listSize.postValue(propertyCount);
            }else {

            List<TargetingParameterList> paramList = mPropertyListDao.getPropertyWithDetails(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(),
                    property.getAuthId() ,keyList, valueList);
            listSize.postValue(paramList.size());
            }
            //check if properties exists
        });
        return listSize;
    }

    public MutableLiveData<Integer> deleteProperty(Property property) {
        MutableLiveData<Integer> propertyID = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            mTargetingParamDao.deleteAll(property.getId());
           int propertyId =  mPropertyListDao.deleteProperty(property.getId());
           propertyID.postValue(propertyId);

        });
        return propertyID;
    }
}

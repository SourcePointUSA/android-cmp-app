package com.sourcepointmeta.app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.models.TargetingParameterList;

import java.util.List;

@Dao
public interface PropertyListDao {

    @Query("SELECT * FROM Property ORDER BY id DESC")
    LiveData<List<Property>> getProperties();

    @Query("SELECT * FROM Property ORDER BY id DESC")
    List<Property> getAllProperties();

    @Insert
    long insert(Property property);


    @Query("UPDATE Property SET accountId= :accountID, propertyId= :propertyID ,property= :propertyName, pmId= :pmID ,staging= :isStaging, showPM= :isShowPM , authId= :authId WHERE id= :id ")
    int update(int accountID, int propertyID, String propertyName, String pmID, boolean isStaging, boolean isShowPM, String authId, long id);

    @Query("SELECT * FROM Property WHERE id= :ID")
    LiveData<Property> getPropertyByID(long ID);

   @Query( "select \n" +
           "(SELECT group_concat( Tp.mkey, ',') \n" +
           " \tFROM \n" +
           "\t(Select mkey FROM targeting_param WHERE refid =W.id order by mKey ASC) as TP) \n" +
           " AS keyList, \n" +
           "(select group_concat( Tp.mValue, ',') \n" +
           " \tFROM \n" +
           " \t(Select mvalue FROM targeting_param WHERE refid =W.id order by mKey ASC) as TP) \n" +
           " AS valueList\n" +
           "from Property as W\n" +
           "where \n" +
           "keyList = :keyList " +
           "AND\n" +
           "valueList = :valueList " +
           "AND accountId= :accountID " +
           "AND propertyId= :propertyID " +
           "AND property= :propertyName " +
           "AND pmId= :pmID " +
           "AND authId= :authId " +
           "AND staging= :isStaging " +
           "AND showPM= :isShowPM ")
   List<TargetingParameterList> getPropertyWithDetails(int accountID, int propertyID, String propertyName, String pmID, boolean isStaging, boolean isShowPM, String authId, String keyList, String valueList);

    @Query( "SELECT count(*) FROM Property as P LEFT JOIN 'targeting_param' as TP on P.id=TP.refID WHERE " +
            "P.accountId= :accountID " +
            "AND P.propertyId= :propertyID " +
            "AND P.property= :propertyName " +
            "AND P.pmId= :pmID " +
            "AND P.staging= :isStaging " +
            "AND P.showPM= :isShowPM " +
            "AND P.authId= :authId " +
            "AND TP.id  IS NULL")
   int getPropertyWithDetails(int accountID, int propertyID, String propertyName, String pmID, boolean isStaging, boolean isShowPM, String authId);

    @Query("DELETE FROM Property WHERE id= :id")
    int deleteProperty(long id);
}

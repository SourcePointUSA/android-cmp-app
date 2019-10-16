package com.sourcepointmeta.app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.models.TargetingParameterList;

import java.util.List;

@Dao
public interface WebsiteListDao {

    @Query("SELECT * FROM websites ORDER BY id DESC")
    LiveData<List<Website>> getAllWebsites();

    @Query("SELECT * FROM websites ORDER BY id DESC")
    List<Website> getAllSites();

    @Insert
    long insert(Website website);


    @Query("UPDATE websites SET accountId= :accountID, siteId= :siteID ,name= :siteName, pmId= :pmID ,staging= :isStaging, showPM= :isShowPM , authId= :authId WHERE id= :id ")
    int update(int accountID, int siteID, String siteName, String pmID, boolean isStaging, boolean isShowPM, String authId, long id);

    @Query("SELECT * FROM websites WHERE id= :ID")
    LiveData<Website> getWebsiteByID(long ID);

   @Query( "select \n" +
           "(SELECT group_concat( Tp.mkey, ',') \n" +
           " \tFROM \n" +
           "\t(Select mkey FROM targeting_param WHERE refid =W.id order by mKey ASC) as TP) \n" +
           " AS keyList, \n" +
           "(select group_concat( Tp.mValue, ',') \n" +
           " \tFROM \n" +
           " \t(Select mvalue FROM targeting_param WHERE refid =W.id order by mKey ASC) as TP) \n" +
           " AS valueList\n" +
           "from websites as W\n" +
           "where \n" +
           "keyList = :keyList " +
           "AND\n" +
           "valueList = :valueList " +
           "AND accountId= :accountID " +
           "AND siteId= :siteID " +
           "AND name= :siteName " +
           "AND pmId= :pmID " +
           "AND authId= :authId " +
           "AND staging= :isStaging " +
           "AND showPM= :isShowPM ")
   List<TargetingParameterList> getWebsiteWithDetails(int accountID, int siteID, String siteName, String pmID, boolean isStaging, boolean isShowPM, String authId, String keyList, String valueList);

    @Query( "SELECT count(*) FROM 'websites' as W LEFT JOIN 'targeting_param' as TP on W.id=TP.refID WHERE " +
            "W.accountId= :accountID " +
            "AND W.siteId= :siteID " +
            "AND W.name= :siteName " +
            "AND W.pmId= :pmID " +
            "AND W.staging= :isStaging " +
            "AND W.showPM= :isShowPM " +
            "AND W.authId= :authId " +
            "AND TP.id  IS NULL")
   int getWebsiteWithDetails(int accountID, int siteID, String siteName, String pmID, boolean isStaging, boolean isShowPM, String authId);

    @Query("DELETE FROM websites WHERE id= :id")
    int deleteWebsite(long id);
}

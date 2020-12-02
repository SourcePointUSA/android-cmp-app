package com.sourcepointmeta.metaapp.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sourcepointmeta.metaapp.database.entity.TargetingParam;

import java.util.List;

@Dao
public interface TargetingParamDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<TargetingParam> targetingParam);

    @Query("SELECT * FROM targeting_param WHERE refID= :id ")
    List<TargetingParam> getAllTargetingParam(long id);

    @Update (onConflict = OnConflictStrategy.REPLACE)
    int updateAll(List<TargetingParam> targetingParamList);

    @Query("DELETE FROM targeting_param WHERE refID= :id")
    int deleteAll(long id);

    @Query("DELETE FROM targeting_param WHERE id= :id")
    int deleteParameter(long id);

    @Query("UPDATE targeting_param SET mValue= :value  WHERE mKey= :key AND refID= :refID")
    int updateParameter(String key, String value, long refID);

    @Insert
    void insertParameter(TargetingParam targetingParam);

}

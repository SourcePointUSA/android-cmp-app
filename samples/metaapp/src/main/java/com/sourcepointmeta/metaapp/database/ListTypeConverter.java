package com.sourcepointmeta.metaapp.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sourcepointmeta.metaapp.database.entity.TargetingParam;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListTypeConverter {

    private Gson gson = new Gson();

    @TypeConverter
    public  List<TargetingParam> stringToTargetingParamList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<TargetingParam>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public  String targetingParamListToString(List<TargetingParam> targetingParamList) {
        return gson.toJson(targetingParamList);
    }
}

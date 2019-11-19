package com.sourcepointmeta.app.database.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.sourcepointmeta.app.database.ListTypeConverter;

import java.util.List;

@Entity (tableName = "websites" )
public class Website implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo( name = "accountId")
    private int accountID;

    @ColumnInfo( name = "siteId")
    private int siteID;

    @ColumnInfo( name = "name")
    private String name;

    @ColumnInfo( name = "pmId")
    private String pmID;

    @ColumnInfo ( name = "staging")
    private boolean isStaging;

    @ColumnInfo( name = "showPM")
    private boolean isShowPM;

    @ColumnInfo (name = "authId")
    private String authId;

    @Ignore
    @ColumnInfo (name = "params_list")
    @TypeConverters(ListTypeConverter.class)
    private List<TargetingParam> targetingParamList;


    public Website(int accountID, int siteID, String name, String pmID, boolean isStaging,boolean isShowPM, String authId) {
        this.accountID = accountID;
        this.siteID = siteID;
        this.name = name;
        this.pmID = pmID;
        this.isStaging = isStaging;
        this.isShowPM = isShowPM;
        this.authId = authId;
    }

    @Ignore
    public Website(int accountID, int siteID, String name, String pmID, boolean isStaging,boolean isShowPM, String authId, List<TargetingParam> targetingParamList) {
        this.accountID = accountID;
        this.siteID = siteID;
        this.name = name;
        this.pmID = pmID;
        this.isStaging = isStaging;
        this.isShowPM = isShowPM;
        this.authId = authId;
        this.targetingParamList = targetingParamList;
    }

    public static final Creator CREATOR = new Creator(){
        public Website createFromParcel(Parcel in) {
            return new Website(in);
        }

        public Website[] newArray(int size) {
            return new Website[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public String getPmID() {
        return pmID;
    }

    public void setPmID(String pmID) {
        this.pmID = pmID;
    }

    public boolean isShowPM() {
        return isShowPM;
    }

    public void setShowPM(boolean showPM) {
        isShowPM = showPM;
    }

    public boolean isStaging() {
        return isStaging;
    }

    public void setStaging(boolean staging) {
        isStaging = staging;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public List<TargetingParam> getTargetingParamList() {
        return targetingParamList;
    }

    public void setTargetingParamList(List<TargetingParam> targetingParamList) {
        this.targetingParamList = targetingParamList;
    }
    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    public Website(Parcel in){
        this.accountID = in.readInt();
        this.siteID = in.readInt();
        this.name = in.readString();
        this.pmID = in.readString();
        this.isStaging = in.readByte() !=0;
        this.isShowPM = in.readByte() !=0;
        this.authId = in.readString();
        this.targetingParamList = (List<TargetingParam>) in.createTypedArrayList(TargetingParam.CREATOR);
    }
    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.accountID);
        dest.writeInt(this.siteID);
        dest.writeString(this.name);
        dest.writeString(this.pmID);
        dest.writeByte((byte)(isStaging?1:0));
        dest.writeByte((byte)(isShowPM?1:0));
        dest.writeString(this.authId);
        dest.writeTypedList(this.targetingParamList);
    }
}

package com.sourcepointmeta.metaapp.database.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.sourcepointmeta.metaapp.database.ListTypeConverter;

import java.util.List;

@Entity (tableName = "property" )
public class Property implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo( name = "accountId")
    private int accountID;

    @ColumnInfo( name = "propertyId")
    private int propertyID;

    @ColumnInfo( name = "property")
    private String property;

    @ColumnInfo( name = "pmId")
    private String pmID;

    @ColumnInfo ( name = "staging")
    private boolean isStaging;

    @ColumnInfo( name = "isNative")
    private boolean isNative;

    @ColumnInfo (name = "authId")
    private String authId;

    @ColumnInfo (name = "message_language")
    private String messageLanguage;

    @Ignore
    @ColumnInfo (name = "params_list")
    @TypeConverters(ListTypeConverter.class)
    private List<TargetingParam> targetingParamList;


    public Property(int accountID, int propertyID, String property, String pmID, boolean isStaging, boolean isNative, String authId, String messageLanguage) {
        this.accountID = accountID;
        this.propertyID = propertyID;
        this.property = property;
        this.pmID = pmID;
        this.isStaging = isStaging;
        this.isNative = isNative;
        this.authId = authId;
        this.messageLanguage = messageLanguage;
    }

    @Ignore
    public Property(int accountID, int propertyID, String property, String pmID, boolean isStaging, boolean isNative, String authId, String selectedLanguage, List<TargetingParam> targetingParamList) {
        this.accountID = accountID;
        this.propertyID = propertyID;
        this.property = property;
        this.pmID = pmID;
        this.isStaging = isStaging;
        this.isNative = isNative;
        this.authId = authId;
        this.messageLanguage = selectedLanguage;
        this.targetingParamList = targetingParamList;
    }

    public static final Creator CREATOR = new Creator(){
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        public Property[] newArray(int size) {
            return new Property[size];
        }
    };


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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

    public int getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public String getPmID() {
        return pmID;
    }

    public void setPmID(String pmID) {
        this.pmID = pmID;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNativeMessage(boolean nativeMessage) {
        isNative = nativeMessage;
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

    public String getMessageLanguage() {
        return messageLanguage;
    }

    public void setMessageLanguage(String messageLanguage) {
        this.messageLanguage = messageLanguage;
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

    public Property(Parcel in){
        this.accountID = in.readInt();
        this.propertyID = in.readInt();
        this.property = in.readString();
        this.pmID = in.readString();
        this.isStaging = in.readByte() !=0;
        this.isNative = in.readByte() !=0;
        this.authId = in.readString();
        this.messageLanguage = in.readString();
        this.targetingParamList = (List<TargetingParam>) in.createTypedArrayList(TargetingParam.CREATOR);
    }
    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.accountID);
        dest.writeInt(this.propertyID);
        dest.writeString(this.property);
        dest.writeString(this.pmID);
        dest.writeByte((byte)(isStaging?1:0));
        dest.writeByte((byte)(isNative?1:0));
        dest.writeString(this.authId);
        dest.writeString(this.messageLanguage);
        dest.writeTypedList(this.targetingParamList);
    }
}

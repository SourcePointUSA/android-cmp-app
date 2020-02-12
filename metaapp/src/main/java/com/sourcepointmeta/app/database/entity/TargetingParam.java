package com.sourcepointmeta.app.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "targeting_param",
        indices = {@Index(value = {"mKey", "refID"}, unique = true)},
        foreignKeys = {@ForeignKey(entity = Property.class,
                parentColumns = "id",
                childColumns = "refID",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)}
)
public class TargetingParam implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "mKey")
    private String mKey;
    @ColumnInfo(name = "mValue")
    private String mValue;
    @ColumnInfo(name = "refID")
    private long refID;

    @Ignore
    public TargetingParam(String key, String value) {
        this.mKey = key;
        this.mValue = value;
    }


    public TargetingParam(String key, String value, long refID) {
        this.mKey = key;
        this.mValue = value;
        this.refID = refID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    public long getRefID() {
        return refID;
    }

    public void setRefID(long refID) {
        this.refID = refID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.mKey.equals(((TargetingParam) obj).mKey))
            return true;
        return super.equals(obj);
    }

    public static final Creator CREATOR = new Creator() {
        public TargetingParam createFromParcel(Parcel in) {
            return new TargetingParam(in);
        }

        public TargetingParam[] newArray(int size) {
            return new TargetingParam[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public TargetingParam(Parcel in) {
        this.mKey = in.readString();
        this.mValue = in.readString();
        this.refID = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
        dest.writeString(this.mValue);
        dest.writeLong(refID);
    }
}

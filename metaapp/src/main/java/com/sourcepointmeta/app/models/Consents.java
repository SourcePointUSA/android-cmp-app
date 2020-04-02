package com.sourcepointmeta.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Consents  implements Parcelable {

    private  String id;
    private  String name;
    private  String type;

    public Consents(String id, String name, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    protected Consents(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<Consents> CREATOR = new Creator<Consents>() {
        @Override
        public Consents createFromParcel(Parcel in) {
            return new Consents(in);
        }

        @Override
        public Consents[] newArray(int size) {
            return new Consents[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
    }
}

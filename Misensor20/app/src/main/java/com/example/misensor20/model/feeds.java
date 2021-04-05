package com.example.misensor20.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class feeds implements Parcelable {
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("entry_id")
    @Expose
    private Integer entryId;
    @SerializedName("field1")
    @Expose
    private String field1;
    @SerializedName("field2")
    @Expose
    private String field2;

    @SerializedName("field3")
    @Expose
    private String field3;

    public feeds(String createdAt, Integer entryId, String field1, String field2, String field3) {
        this.createdAt = createdAt;
        this.entryId = entryId;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public feeds(Parcel in) {
        createdAt = in.readString();
        entryId = in.readInt();
        field1 = in.readString();
        field2 = in.readString();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(entryId);

        dest.writeString(createdAt);

        dest.writeString(field1);

        dest.writeString(field2);

        dest.writeString(field3);
    }

    public static final Parcelable.Creator<feeds> CREATOR = new Parcelable.Creator<feeds>()
    {
        public feeds createFromParcel(Parcel in)
        {
            return new feeds(in);
        }
        public feeds[] newArray(int size)
        {
            return new feeds[size];
        }
    };
}

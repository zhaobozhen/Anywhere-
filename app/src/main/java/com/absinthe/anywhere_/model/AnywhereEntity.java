package com.absinthe.anywhere_.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "anywhere_table")
public class AnywhereEntity implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String mId;

    @NonNull
    @ColumnInfo(name = "app_name")
    private String mAppName;

    @NonNull
    @ColumnInfo(name = "param_1")
    private String mParam1;

    @ColumnInfo(name = "param_2")
    private String mParam2;

    @ColumnInfo(name = "param_3")
    private String mParam3;

    @ColumnInfo(name = "description")
    private String mDescription;

    @NonNull
    @ColumnInfo(name = "type")
    private Integer mType;

    @ColumnInfo(name = "category")
    private String mCategory;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    public static AnywhereEntity Builder() {
        String time = System.currentTimeMillis() + "";
        return new AnywhereEntity(
                time,
                "",
                "",
                "",
                "",
                "",
                AnywhereType.ACTIVITY,
                GlobalValues.sCategory,
                time);
    }

    public AnywhereEntity(@NonNull String id, @NonNull String appName, @NonNull String param1,
                          String param2, String param3, String description,
                          @NonNull Integer type, String category, @NonNull String timeStamp) {
        mId = id;
        mAppName = appName;
        mParam1 = param1;
        mParam2 = param2;
        mParam3 = param3;
        mDescription = description;
        mType = type;
        mCategory = category;
        mTimeStamp = timeStamp;
    }

    private AnywhereEntity(Parcel in) {
        mId = Objects.requireNonNull(in.readString());
        mAppName = Objects.requireNonNull(in.readString());
        mParam1 = Objects.requireNonNull(in.readString());
        mParam2 = in.readString();
        mParam3 = in.readString();
        mDescription = in.readString();
        mType = in.readInt();
        mCategory = in.readString();
        mTimeStamp = Objects.requireNonNull(in.readString());
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getAppName() {
        return mAppName;
    }

    @NonNull
    public String getParam1() {
        return mParam1;
    }

    public String getParam2() {
        return mParam2;
    }

    public String getParam3() {
        return mParam3;
    }

    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public Integer getType() {
        return mType;
    }

    @NonNull
    public Integer getAnywhereType() {
        return mType % 10;
    }

    @NonNull
    public Integer getShortcutType() {
        return (mType / 10) % 10;
    }

    @NonNull
    public Integer getExportedType() {
        return mType / 100;
    }

    @NonNull
    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(@NonNull String mCategory) {
        this.mCategory = mCategory;
    }

    public void setId(@NonNull String mId) {
        this.mId = mId;
    }

    public void setAppName(@NonNull String mAppName) {
        this.mAppName = mAppName;
    }

    public void setParam1(@NonNull String mParam1) {
        this.mParam1 = mParam1;
    }

    public void setParam2(@NonNull String mParam2) {
        this.mParam2 = mParam2;
    }

    public void setParam3(@NonNull String mParam3) {
        this.mParam3 = mParam3;
    }

    public void setDescription(@NonNull String mDescription) {
        this.mDescription = mDescription;
    }

    public void setType(@NonNull Integer mType) {
        this.mType = mType;
    }

    public void setTimeStamp(@NonNull String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAppName);
        dest.writeString(mParam1);
        dest.writeString(mParam2);
        dest.writeString(mParam3);
        dest.writeString(mDescription);
        dest.writeInt(mType);
        dest.writeString(mCategory);
        dest.writeString(mTimeStamp);
    }

    public static final Parcelable.Creator<AnywhereEntity> CREATOR = new Parcelable.Creator<AnywhereEntity>() {

        @Override
        public AnywhereEntity createFromParcel(Parcel source) {
            return new AnywhereEntity(source);
        }

        @Override
        public AnywhereEntity[] newArray(int size) {
            return new AnywhereEntity[size];
        }
    };
}

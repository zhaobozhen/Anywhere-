package com.absinthe.anywhere_.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "anywhere_table")
public class AnywhereEntity {

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

    @NonNull
    @ColumnInfo(name = "param_2")
    private String mParam2;

    @NonNull
    @ColumnInfo(name = "param_3")
    private String mParam3;

    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;

    @NonNull
    @ColumnInfo(name = "type")
    private Integer mType;

    @NonNull
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
                          @NonNull String param2, @NonNull String param3, @NonNull String description,
                          @NonNull Integer type, @NonNull String category, @NonNull String timeStamp) {
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

    public AnywhereEntity setCategory(@NonNull String mCategory) {
        this.mCategory = mCategory;
        return this;
    }

    public AnywhereEntity setId(@NonNull String mId) {
        this.mId = mId;
        return this;
    }

    public AnywhereEntity setAppName(@NonNull String mAppName) {
        this.mAppName = mAppName;
        return this;
    }

    public AnywhereEntity setParam1(@NonNull String mParam1) {
        this.mParam1 = mParam1;
        return this;
    }

    public AnywhereEntity setParam2(@NonNull String mParam2) {
        this.mParam2 = mParam2;
        return this;
    }

    public AnywhereEntity setParam3(@NonNull String mParam3) {
        this.mParam3 = mParam3;
        return this;
    }

    public AnywhereEntity setDescription(@NonNull String mDescription) {
        this.mDescription = mDescription;
        return this;
    }

    public AnywhereEntity setType(@NonNull Integer mType) {
        this.mType = mType;
        return this;
    }

    public AnywhereEntity setTimeStamp(@NonNull String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
        return this;
    }
}

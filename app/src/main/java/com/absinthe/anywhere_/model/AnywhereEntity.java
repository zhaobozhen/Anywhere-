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

    @ColumnInfo(name = "param_2")
    private String mParam2;

    @ColumnInfo(name = "param_3")
    private String mParam3;

    @ColumnInfo(name = "description")
    private String mDescription;

    @NonNull
    @ColumnInfo(name = "type")
    private Integer mType;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    public AnywhereEntity(@NonNull String id, @NonNull String appName, @NonNull String param1, String param2, String param3, String description, @NonNull Integer type, @NonNull String timeStamp) {
        mId = id;
        mAppName = appName;
        mParam1 = param1;
        mParam2 = param2;
        mParam3 = param3;
        mDescription = description;
        mType = type;
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
        return mType / 10;
    }

    @NonNull
    public Integer getExportedType() {
        return mType / 100;
    }

    @NonNull
    public String getTimeStamp() {
        return mTimeStamp;
    }
}

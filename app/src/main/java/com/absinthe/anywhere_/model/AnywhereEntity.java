package com.absinthe.anywhere_.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "anywhere_table", primaryKeys = {"package_name", "class_name", "url_scheme"})
public class AnywhereEntity {

    @NonNull
    @ColumnInfo(name = "package_name")
    private String mPackageName;

    @NonNull
    @ColumnInfo(name = "class_name")
    private String mClassName;

    @NonNull
    @ColumnInfo(name = "app_name")
    private String mAppName;

    @NonNull
    @ColumnInfo(name = "custom_texture")
    private String mCustomTexture;

    @NonNull
    @ColumnInfo(name = "class_name_type")
    private Integer mClassNameType;

    @NonNull
    @ColumnInfo(name = "url_scheme")
    private String mUrlScheme;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    public AnywhereEntity(@NonNull String packageName, @NonNull String className, @NonNull Integer classNameType, @NonNull String urlScheme, @NonNull String appName, @NonNull String customTexture, @NonNull String timeStamp) {
        mPackageName = packageName;
        mClassName = className;
        mClassNameType = classNameType;
        mUrlScheme = urlScheme;
        mAppName = appName;
        mCustomTexture = customTexture;
        mTimeStamp = timeStamp;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getClassName() {
        return this.mClassName;
    }

    public Integer getClassNameType() {
        return this.mClassNameType;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public String getCustomTexture() {
        return this.mCustomTexture;
    }

    public String getUrlScheme() {
        return this.mUrlScheme;
    }

    public String getTimeStamp() {
        return this.mTimeStamp;
    }
}

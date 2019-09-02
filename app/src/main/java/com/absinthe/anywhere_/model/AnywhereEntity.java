package com.absinthe.anywhere_.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.absinthe.anywhere_.adapter.SelectableCardsAdapter;

@Entity(tableName = "anywhere_table", primaryKeys = {"package_name", "class_name"})
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
    private int mClassNameType;

    public AnywhereEntity(@NonNull String packageName, @NonNull String className, @NonNull int classNameType, @NonNull String appName, @NonNull String customTexture) {
        mPackageName = packageName;
        mClassName = className;
        mClassNameType = classNameType;
        mAppName = appName;
        mCustomTexture = customTexture;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getClassName() {
        return this.mClassName;
    }

    public int getClassNameType() {
        return this.mClassNameType;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public String getCustomTexture() {
        return this.mCustomTexture;
    }

}

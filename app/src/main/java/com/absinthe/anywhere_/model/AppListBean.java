package com.absinthe.anywhere_.model;

import android.graphics.drawable.Drawable;

public class AppListBean {
    private String mAppName;
    private String mPackageName;
    private String mClassName;
    private Drawable mIcon;
    private int mType;

    public AppListBean() {
        mAppName = "";
        mPackageName = "";
        mClassName = "";
        mType = AnywhereType.URL_SCHEME;
    }

    public AppListBean(String appName, String packageName, String className, int type) {
        this.mAppName = appName;
        this.mPackageName = packageName;
        this.mClassName = className;
        this.mType = type;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }
}

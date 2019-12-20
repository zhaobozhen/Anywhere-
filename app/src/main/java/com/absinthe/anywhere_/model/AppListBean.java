package com.absinthe.anywhere_.model;

import android.graphics.drawable.Drawable;

public class AppListBean {
    private String mAppName;
    private String mPackageName;
    private String mClassName;
    private Drawable mIcon;

    public AppListBean() {
        mAppName = "";
        mPackageName = "";
        mClassName = "";
    }

    public AppListBean(String appName, String packageName, String className) {
        this.mAppName = appName;
        this.mPackageName = packageName;
        this.mClassName = className;
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
}

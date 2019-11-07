package com.absinthe.anywhere_.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppListBean {
    private String appName;
    private String packageName;
    private String className;
    private Drawable icon;

    public AppListBean() {
        appName = "";
        packageName = "";
        className = "";
    }

    public AppListBean(String appName, String packageName, String className) {
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}

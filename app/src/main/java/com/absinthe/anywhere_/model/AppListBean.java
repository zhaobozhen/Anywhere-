package com.absinthe.anywhere_.model;

public class AppListBean {
    private String appName;
    private String packageName;

    public AppListBean() {
        appName = "";
        packageName = "";
    }

    public AppListBean(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
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
}

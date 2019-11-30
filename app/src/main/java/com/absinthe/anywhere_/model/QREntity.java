package com.absinthe.anywhere_.model;

import com.absinthe.anywhere_.interfaces.OnQRLaunchedListener;

public class QREntity {
    private String pkgName;
    private String clsName;
    private String urlScheme;
    private OnQRLaunchedListener listener;

    QREntity(OnQRLaunchedListener listener) {
        this.listener = listener;
    }

    public void launch() {
        listener.onLaunched();
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getUrlScheme() {
        return urlScheme;
    }

    public void setUrlScheme(String urlScheme) {
        this.urlScheme = urlScheme;
    }
}

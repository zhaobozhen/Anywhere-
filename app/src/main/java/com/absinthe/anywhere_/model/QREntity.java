package com.absinthe.anywhere_.model;

import com.absinthe.anywhere_.interfaces.OnQRLaunchedListener;

public class QREntity {
    private String mPkgName;
    private String mClsName;
    private String mUrlScheme;
    private OnQRLaunchedListener mListener;

    QREntity(OnQRLaunchedListener listener) {
        this.mListener = listener;
    }

    public void launch() {
        mListener.onLaunched();
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public String getClsName() {
        return mClsName;
    }

    public void setClsName(String mClsName) {
        this.mClsName = mClsName;
    }

    public String getUrlScheme() {
        return mUrlScheme;
    }

    public void setUrlScheme(String mUrlScheme) {
        this.mUrlScheme = mUrlScheme;
    }

    public OnQRLaunchedListener getListener() {
        return mListener;
    }

    public void setListener(OnQRLaunchedListener mListener) {
        this.mListener = mListener;
    }
}

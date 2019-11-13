package com.absinthe.anywhere_.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SerializableAnywhereEntity implements Serializable {
    @SerializedName("id")
    private String mId;

    @SerializedName("appName")
    private String mAppName;

    @SerializedName("param1")
    private String mParam1;

    @SerializedName("param2")
    private String mParam2;

    @SerializedName("param3")
    private String mParam3;

    @SerializedName("desc")
    private String mDescription;

    @SerializedName("type")
    private Integer mType;

    @SerializedName("timeStamp")
    private String mTimeStamp;

    public SerializableAnywhereEntity() {
    }

    public SerializableAnywhereEntity(AnywhereEntity ae) {
        this.mId = ae.getId();
        this.mAppName = ae.getAppName();
        this.mParam1 = ae.getParam1();
        this.mParam2 = ae.getParam2();
        this.mParam3 = ae.getParam3();
        this.mDescription = ae.getDescription();
        this.mType = ae.getType();
        this.mTimeStamp = ae.getTimeStamp();
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmParam1() {
        return mParam1;
    }

    public void setmParam1(String mParam1) {
        this.mParam1 = mParam1;
    }

    public String getmParam2() {
        return mParam2;
    }

    public void setmParam2(String mParam2) {
        this.mParam2 = mParam2;
    }

    public String getmParam3() {
        return mParam3;
    }

    public void setmParam3(String mParam3) {
        this.mParam3 = mParam3;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Integer getmType() {
        return mType;
    }

    public void setmType(Integer mType) {
        this.mType = mType;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public Integer getAnywhereType() {
        return mType % 10;
    }

    public Integer getShortcutType() {
        return mType / 10;
    }
}

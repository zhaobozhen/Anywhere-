package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

public class GiftModel {

    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("alipayAccount")
    private String alipayAccount;

    @SerializedName("activeTimes")
    private short activeTimes;

    @SerializedName("isActive")
    private short isActive;

    @SerializedName("timeStamp")
    private String timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public short getActiveTimes() {
        return activeTimes;
    }

    public void setActiveTimes(short activeTimes) {
        this.activeTimes = activeTimes;
    }

    public short getIsActive() {
        return isActive;
    }

    public void setIsActive(short isActive) {
        this.isActive = isActive;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}

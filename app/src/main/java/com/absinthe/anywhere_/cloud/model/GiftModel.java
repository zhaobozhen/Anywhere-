package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

public class GiftModel {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("msg")
    private String msg;

    @SerializedName("token")
    private String token;

    @SerializedName("data")
    private Data data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("id")
        public int id;

        @SerializedName("code")
        public String code;

        @SerializedName("ssaid")
        public String ssaid;

        @SerializedName("alipayAccount")
        public String alipayAccount;

        @SerializedName("activeTimes")
        public short activeTimes;

        @SerializedName("isActive")
        public short isActive;

        @SerializedName("timeStamp")
        public String timeStamp;
    }
}

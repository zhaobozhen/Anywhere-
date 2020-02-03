package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftModel {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("msg")
    private String msg;

    @SerializedName("token")
    private String token;

    @SerializedName("data")
    private List<Data> data;

    public class Data {

        @SerializedName("id")
        private int id;

        @SerializedName("code")
        private String code;

        @SerializedName("ssaid")
        private String ssaid;

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

        public String getSsaid() {
            return ssaid;
        }

        public void setSsaid(String ssaid) {
            this.ssaid = ssaid;
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

    public Data getData(int pos) {
        if (data.size() == 0) {
            return null;
        }
        return data.get(pos);
    }

    public void setData(Data data, int pos) {
        this.data.set(pos, data);
    }

}

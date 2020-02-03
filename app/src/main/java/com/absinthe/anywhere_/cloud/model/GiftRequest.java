package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

public class GiftRequest {

    @SerializedName("code")
    private String code;

    @SerializedName("ssaid")
    private String ssaid;

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
}

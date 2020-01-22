package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

public class GiftRequest {

    @SerializedName("code")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

package com.absinthe.anywhere_.cloud.interfaces;

import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.utils.manager.URLManager;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GiftRequest {
    @POST(URLManager.GIFT_SCF_URL)
    @FormUrlEncoded
    Call<GiftModel> requestByCode(
            @Field("code") String code,
            @Field("ssaid") String ssaid);
}

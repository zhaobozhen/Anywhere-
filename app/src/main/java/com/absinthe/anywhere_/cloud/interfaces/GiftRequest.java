package com.absinthe.anywhere_.cloud.interfaces;

import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.cloud.model.GiftPriceModel;
import com.absinthe.anywhere_.utils.manager.URLManager;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GiftRequest {
    @FormUrlEncoded
    @Headers("content-type: application/x-www-form-urlencoded; charset=UTF-8")
    @POST(URLManager.GIFT_API)
    Call<GiftModel> requestByCode(
            @Field("code") String code,
            @Field("ssaid") String ssaid);

    @GET(URLManager.GET_GIFT_PRICE_API)
    Call<GiftPriceModel> requestPrice();
}

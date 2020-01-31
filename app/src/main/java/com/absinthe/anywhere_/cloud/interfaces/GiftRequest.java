package com.absinthe.anywhere_.cloud.interfaces;

import com.absinthe.anywhere_.cloud.model.GiftModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GiftRequest {
    @POST("release/Anywhere-Gift")
    @FormUrlEncoded
    Call<GiftModel> requestByCode(@Field("code") String code);
}

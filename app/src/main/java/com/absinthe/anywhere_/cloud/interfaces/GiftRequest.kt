package com.absinthe.anywhere_.cloud.interfaces

import com.absinthe.anywhere_.cloud.model.GiftModel
import com.absinthe.anywhere_.cloud.model.GiftPriceModel
import com.absinthe.anywhere_.utils.manager.URLManager
import retrofit2.Call
import retrofit2.http.*

interface GiftRequest {
    @FormUrlEncoded
    @Headers("content-type: application/x-www-form-urlencoded; charset=UTF-8")
    @POST(URLManager.GIFT_API)
    fun requestByCode(
            @Field("code") code: String?,
            @Field("ssaid") ssaid: String?): Call<GiftModel?>?

    @GET(URLManager.GET_GIFT_PRICE_API)
    fun requestPrice(): Call<GiftPriceModel?>?
}
package com.absinthe.anywhere_.cloud.model

import com.google.gson.annotations.SerializedName

class GiftModel {
    @SerializedName("statusCode")
    var statusCode = 0

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("data")
    var data: Data? = null

    @SerializedName("token")
    var token: String? = null

    inner class Data {
        @SerializedName("id")
        var id = 0

        @SerializedName("code")
        var code: String? = null

        @SerializedName("ssaid")
        var ssaid: String? = null

        @SerializedName("alipayAccount")
        var alipayAccount: String? = null

        @SerializedName("activeTimes")
        var activeTimes = 0

        @SerializedName("isActive")
        var isActive = 0

        @SerializedName("timeStamp")
        var timeStamp: String? = null
    }
}
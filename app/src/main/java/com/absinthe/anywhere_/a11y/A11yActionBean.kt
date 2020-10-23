package com.absinthe.anywhere_.a11y

import com.google.gson.annotations.SerializedName

data class A11yActionBean(
        @SerializedName("type") val type: Int = A11yType.NONE,
        @SerializedName("content") val content: String = "",
        @SerializedName("activityId") val activityId: String = "",
        @SerializedName("delay") val delay: Long = 0,
        @SerializedName("actionActivity") val actionActivity: String = ""
)
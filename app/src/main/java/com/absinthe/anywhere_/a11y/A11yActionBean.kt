package com.absinthe.anywhere_.a11y

import com.google.gson.annotations.SerializedName

data class A11yActionBean(
        @SerializedName("type") val type: Int = A11yType.NONE,
        @SerializedName("content") var content: String = "",
        @SerializedName("activityId") var activityId: String = "",
        @SerializedName("delay") var delay: Long = 0,
        @SerializedName("actionActivity") var actionActivity: String = "",
        @SerializedName("contains") var contains: Boolean = false
)
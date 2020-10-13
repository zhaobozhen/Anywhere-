package com.absinthe.anywhere_.model.cloud

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RuleEntity(
        @SerializedName("name") val name: String,
        @SerializedName("contributor") val contributor: String,
        @SerializedName("content") val content: String,
        @SerializedName("desc") val desc: String
)
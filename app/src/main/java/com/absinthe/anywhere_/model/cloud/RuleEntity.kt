package com.absinthe.anywhere_.model.cloud

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class RuleEntity(
  @SerializedName("name") val name: String,
  @SerializedName("contributor") val contributor: String,
  @SerializedName("content") val content: String,
  @SerializedName("desc") val desc: String
) : Parcelable

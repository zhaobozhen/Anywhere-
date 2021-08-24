package com.absinthe.anywhere_.a11y

import com.google.gson.annotations.SerializedName

data class A11yEntity(
  @SerializedName("applicationId") var applicationId: String = "",
  @SerializedName("entryActivity") var entryActivity: String = "",
  @SerializedName("actions") var actions: List<A11yActionBean> = listOf()
)

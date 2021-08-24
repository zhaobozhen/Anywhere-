package com.absinthe.anywhere_.model.viewholder

import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.google.gson.annotations.SerializedName

data class FlowStepBean(
  @SerializedName("entity") var entity: AnywhereEntity? = null,
  @SerializedName("delay") var delay: Long = 0
)

package com.absinthe.anywhere_.model

import com.google.gson.annotations.SerializedName

const val TYPE_STRING = "--es"
const val TYPE_BOOLEAN = "--ez"
const val TYPE_INT = "--ei"
const val TYPE_LONG = "--el"
const val TYPE_FLOAT = "--ef"
const val TYPE_DOUBLE = "--ed"
const val TYPE_URI = "--eu"

const val TYPE_STRING_LABEL = "String"
const val TYPE_BOOLEAN_LABEL = "Bool"
const val TYPE_INT_LABEL = "Int"
const val TYPE_LONG_LABEL = "Long"
const val TYPE_FLOAT_LABEL = "Float"
const val TYPE_DOUBLE_LABEL = "Double"
const val TYPE_URI_LABEL = "Uri"

data class ExtraBean(
  @SerializedName("data") val data: String,
  @SerializedName("action") val action: String,
  @SerializedName("category") val category: String = "",
  @SerializedName("extras") val extras: List<ExtraItem>
) {
  override fun toString(): String {
    val sb = StringBuilder()
    if (action.isNotBlank()) {
      sb.append("-a ").append(action)
    }
    if (data.isNotBlank()) {
      sb.append(" -d ").append(data)
    }

    for (extra in extras) {
      sb.append(" ").append(extra.toString())
    }
    return sb.toString()
  }

  data class ExtraItem(
    @SerializedName("type") var type: String,
    @SerializedName("key") var key: String,
    @SerializedName("value") var value: String
  ) {
    override fun toString(): String {
      return "$type \"$key\" \"$value\""
    }
  }
}

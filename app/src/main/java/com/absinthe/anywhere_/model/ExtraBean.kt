package com.absinthe.anywhere_.model

import com.google.gson.annotations.SerializedName
import java.lang.StringBuilder

const val TYPE_STRING = "--es"
const val TYPE_BOOLEAN = "--ez"
const val TYPE_INT = "--ei"
const val TYPE_LONG = "--el"
const val TYPE_FLOAT = "--ef"
const val TYPE_URI = "--eu"

data class ExtraBean(
        @SerializedName("data") val data: String,
        @SerializedName("action") val action: String,
        @SerializedName("category") val category: String = "",
        @SerializedName("extras") val extras: List<ExtraItem>
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("-a ").append(action).append(" -d ").append(data)

        for (extra in extras) {
            sb.append(" ").append(extra.toString())
        }
        return sb.toString()
    }

    data class ExtraItem(
            @SerializedName("type") val type: String,
            @SerializedName("key") val key: String,
            @SerializedName("value") val value: String
    ) {
        override fun toString(): String {
            return "$type \"$key\" \"$value\""
        }
    }
}
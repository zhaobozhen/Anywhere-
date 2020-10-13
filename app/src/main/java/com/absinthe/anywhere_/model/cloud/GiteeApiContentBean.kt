package com.absinthe.anywhere_.model.cloud

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * <pre>
 * author : Absinthe
 * time : 2020/10/12
 * </pre>
 */
@Keep
data class GiteeApiContentBean(
        @SerializedName("type") val type: String,
        @SerializedName("size") val size: Long,
        @SerializedName("name") val name: String,
        @SerializedName("path") val path: String,
        @SerializedName("sha") val sha: String,
        @SerializedName("url") val url: String,
        @SerializedName("html_url") val html_url: String,
        @SerializedName("download_url") val download_url: String?,
        @SerializedName("_links") val _links: Links
) {
    data class Links(
            @SerializedName("self") val self: String,
            @SerializedName("html") val html: String
    )
}
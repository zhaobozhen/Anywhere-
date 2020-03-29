package com.absinthe.anywhere_.utils.manager

import io.michaelrocks.paranoid.Obfuscate

/**
 * URL Manager
 *
 * All URLs in App.
 */
@Obfuscate
object URLManager {
    //General
    const val OLD_DOCUMENT_PAGE = "https://zhaobozhen.github.io/Anywhere-Docs/"
    const val DOCUMENT_PAGE = "https://absinthe.life/Anywhere-Docs/"
    const val SHIZUKU_COOLAPK_DOWNLOAD_PAGE = "https://www.coolapk.com/apk/moe.shizuku.privileged.api/"
    const val SHORTCUT_COMMUNITY_PAGE = "https://sharecuts.cn/apps/"

    //Scheme
    const val ANYWHERE_SCHEME = "anywhere://"
    const val URL_HOST = "url"
    const val OPEN_HOST = "open"
    const val CARD_SHARING_HOST = "share"

    //Gift
    const val DOMAIN = "https://service-65n0wylk-1252542993.gz.apigw.tencentcs.com/release/"
    const val GIFT_API = "Anywhere-Gift"
    const val GET_GIFT_PRICE_API = "GetGiftPrice"
    const val MARKET_URL_SCHEME = "market://details?id=" + "com.absinthe.anywhere_"
}
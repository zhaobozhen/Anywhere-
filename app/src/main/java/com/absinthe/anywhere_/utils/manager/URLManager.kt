package com.absinthe.anywhere_.utils.manager

import com.absinthe.anywhere_.BuildConfig
import com.absinthe.libraries.me.Absinthe

/**
 * URL Manager
 *
 * All URLs in App.
 */
object URLManager {
  //General
  const val DOCUMENT_PAGE = "https://absinthe.life/Anywhere-Docs/"
  const val SHIZUKU_MARKET_URL = Absinthe.MARKET_DETAIL_SCHEME + "moe.shizuku.privileged.api"
  const val SHORTCUT_COMMUNITY_PAGE = "https://sharecuts.cn/apps/"
  const val BETA_DISTRIBUTE_URL =
    "https://install.appcenter.ms/users/zhaobozhen2025-gmail.com/apps/anywhere/distribution_groups/public"
  const val ANYWHERE_MARKET_URL = Absinthe.MARKET_DETAIL_SCHEME + BuildConfig.APPLICATION_ID

  //Scheme
  const val ANYWHERE_SCHEME = "anywhere://"
  const val ANYWHERE_SCHEME_RAW = "anywhere"
  const val URL_HOST = "url"
  const val OPEN_HOST = "open"
  const val CARD_SHARING_HOST = "share"

  //WebDAV
  const val BACKUP_DIR = "Anywhere-/Backup/"
}

package com.absinthe.anywhere_.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.utils.SPUtils
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.manager.IconPackManager
import com.absinthe.anywhere_.utils.manager.IconPackManager.IconPack
import com.tencent.mmkv.MMKV
import jonathanfinerty.once.Once
import java.text.SimpleDateFormat
import java.util.*

object Settings {

  var iconPackManager: IconPackManager = IconPackManager()
  var iconPack: IconPack? = null

  val date: String by lazy {
    val date = Date()
    val dateFormat = SimpleDateFormat("MM-dd", Locale.CHINA)
    dateFormat.format(date)
  }

  fun init(context: Context) {
    setLogger()
    initIconPackManager(context)
  }

  fun getTheme(): Int {
    return when (GlobalValues.darkMode) {
      Const.DARK_MODE_OFF, "" -> AppCompatDelegate.MODE_NIGHT_NO
      Const.DARK_MODE_ON -> AppCompatDelegate.MODE_NIGHT_YES
      Const.DARK_MODE_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
      Const.DARK_MODE_BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
      Const.DARK_MODE_AUTO -> UxUtils.getAutoDarkMode()
      else -> AppCompatDelegate.MODE_NIGHT_NO
    }
  }

  fun setLogger() {
    GlobalValues.sIsDebugMode = BuildConfig.DEBUG or GlobalValues.sIsDebugMode
  }

  fun initIconPackManager(context: Context) {
    iconPackManager.setContext(context)
    val hashMap = iconPackManager.getAvailableIconPacks(true)

    for ((key, value) in hashMap) {
      if (key == GlobalValues.iconPack) {
        iconPack = value
        break
      }
    }
  }

  fun initMMKV(application: AnywhereApplication) {
    MMKV.initialize(application)

    if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.MMKV_MIGRATE)) {
      val sp = application.getSharedPreferences(SPUtils.sPName, MODE_PRIVATE)
      MMKV.mmkvWithID(SPUtils.sPName)?.importFromSharedPreferences(sp)
      Once.markDone(OnceTag.MMKV_MIGRATE)
    }
  }
}

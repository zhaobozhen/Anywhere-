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
import com.absinthe.anywhere_.utils.StorageUtils.getTokenFromFile
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.manager.IconPackManager
import com.absinthe.anywhere_.utils.manager.IconPackManager.IconPack
import com.tencent.mmkv.MMKV
import jonathanfinerty.once.Once
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Settings {

    var sIconPackManager: IconPackManager = IconPackManager()
    var sIconPack: IconPack? = null

    lateinit var sDate: String
    lateinit var sToken: String

    fun init(context: Context) {
        setLogger()
        setTheme(GlobalValues.darkMode)
        initIconPackManager(context)
        setDate()
        initToken(context)
    }

    fun setTheme(mode: String) {
        when (mode) {
            Const.DARK_MODE_OFF, "" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Const.DARK_MODE_ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Const.DARK_MODE_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Const.DARK_MODE_BATTERY -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            Const.DARK_MODE_AUTO -> AppCompatDelegate.setDefaultNightMode(UxUtils.getAutoDarkMode())
        }
    }

    fun setLogger() {
        GlobalValues.sIsDebugMode = BuildConfig.DEBUG or GlobalValues.sIsDebugMode
    }

    fun initIconPackManager(context: Context) {
        sIconPackManager.setContext(context)
        val hashMap = sIconPackManager.getAvailableIconPacks(true)

        for ((key, value) in hashMap) {
            if (key == GlobalValues.iconPack) {
                sIconPack = value
                break
            }
        }
    }

    fun initMMKV(application: AnywhereApplication) {
        MMKV.initialize(application)

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.MMKV_MIGRATE)) {
            val sp = application.getSharedPreferences(SPUtils.sPName, MODE_PRIVATE)
            MMKV.mmkvWithID(SPUtils.sPName).importFromSharedPreferences(sp)
            Once.markDone(OnceTag.MMKV_MIGRATE)
        }
    }

    private fun setDate() {
        val date = Date()
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        sDate = dateFormat.format(date)
    }

    private fun initToken(context: Context) {
        sToken = try {
            getTokenFromFile(context)
        } catch (e: IOException) {
            ""
        }
    }
}
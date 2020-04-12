package com.absinthe.anywhere_.model

import androidx.appcompat.app.AppCompatDelegate
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.model.GlobalValues.setsIconPack
import com.absinthe.anywhere_.utils.StorageUtils.getTokenFromFile
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.manager.IconPackManager
import com.absinthe.anywhere_.utils.manager.IconPackManager.IconPack
import com.blankj.utilcode.util.Utils
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Settings {

    var sIconPackManager: IconPackManager = IconPackManager()

    lateinit var sIconPack: IconPack
    lateinit var sDate: String
    lateinit var sToken: String

    const val DEFAULT_ICON_PACK = "default.icon.pack"

    fun init() {
        setLogger()
        setTheme(GlobalValues.sDarkMode)
        initIconPackManager()
        setDate()
        initToken()
    }

    fun release() {
        sIconPackManager.setContext(null)
    }

    fun setTheme(mode: String?) {
        when (mode) {
            Const.DARK_MODE_OFF, "" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Const.DARK_MODE_ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Const.DARK_MODE_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Const.DARK_MODE_BATTERY -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            Const.DARK_MODE_AUTO -> AppCompatDelegate.setDefaultNightMode(UiUtils.getAutoDarkMode())
        }
    }

    fun setLogger() {
        GlobalValues.sIsDebugMode = BuildConfig.DEBUG or GlobalValues.sIsDebugMode
    }

    fun initIconPackManager() {
        sIconPackManager.setContext(Utils.getApp())
        val hashMap = sIconPackManager.getAvailableIconPacks(true)

        setsIconPack(DEFAULT_ICON_PACK)
        for ((key, value) in hashMap) {
            if (key == GlobalValues.sIconPack) {
                sIconPack = value
                break
            }
        }
    }

    private fun setDate() {
        val date = Date()
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        sDate = dateFormat.format(date)
    }

    private fun initToken() {
        sToken = try {
            getTokenFromFile(Utils.getApp())
        } catch (e: IOException) {
            ""
        }
    }
}
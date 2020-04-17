package com.absinthe.anywhere_.constants

import android.content.Context
import android.text.Html
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.utils.SPUtils.getBoolean
import com.absinthe.anywhere_.utils.SPUtils.getInt
import com.absinthe.anywhere_.utils.SPUtils.getLong
import com.absinthe.anywhere_.utils.SPUtils.getString
import com.absinthe.anywhere_.utils.SPUtils.putBoolean
import com.absinthe.anywhere_.utils.SPUtils.putInt
import com.absinthe.anywhere_.utils.SPUtils.putLong
import com.absinthe.anywhere_.utils.SPUtils.putString
import com.absinthe.anywhere_.utils.TextUtils
import com.blankj.utilcode.util.Utils

object GlobalValues {

    @JvmField
     var sIsDebugMode = false

    @JvmField
    var sIsStreamCardMode = false

    @JvmField
    var sIsStreamCardModeSingleLine = false

    @JvmField
    var sIsMd2Toolbar = false

    @JvmField
    var sIsPages = false

    @JvmField
    var sIsCollectorPlus = false
    var sIsExcludeFromRecent = false

    @JvmField
    var sIsShowShellResult = false

    @JvmField
    var sWorkingMode: MutableLiveData<String>? = MutableLiveData()

    lateinit var sActionBarType: String
    lateinit var sDarkMode: String
    lateinit var sBackgroundUri: String
    lateinit var sCardBackgroundMode: String
    lateinit var sSortMode: String
    lateinit var sIconPack: String
    lateinit var sCategory: String
    lateinit var sDefrostMode: String

    var sCurrentPage = 0

    @JvmField
    var sDumpInterval = 0

    @JvmField
    var sAutoDarkModeStart: Long = 0

    @JvmField
    var sAutoDarkModeEnd: Long = 0

    @JvmStatic

    fun init(context: Context) {
        sIsDebugMode = false
        sIsStreamCardMode = getBoolean(context, Const.PREF_STREAM_CARD_MODE, false)
        sIsStreamCardModeSingleLine = getBoolean(context, Const.PREF_STREAM_CARD_SINGLE_LINE, false)
        sIsMd2Toolbar = getBoolean(context, Const.PREF_MD2_TOOLBAR, false)
        sIsPages = getBoolean(context, Const.PREF_PAGES, false)
        sIsCollectorPlus = getBoolean(context, Const.PREF_COLLECTOR_PLUS, false)
        sIsExcludeFromRecent = getBoolean(context, Const.PREF_EXCLUDE_FROM_RECENT, false)
        sIsShowShellResult = getBoolean(context, Const.PREF_SHOW_SHELL_RESULT, false)
        sWorkingMode?.value = getString(context, Const.PREF_WORKING_MODE)
        sActionBarType = getString(context, Const.PREF_ACTION_BAR_TYPE)
        sDarkMode = getString(context, Const.PREF_DARK_MODE)
        sBackgroundUri = getString(context, Const.PREF_CHANGE_BACKGROUND)
        sCardBackgroundMode = getString(context, Const.PREF_CARD_BACKGROUND, "off")
        sSortMode = getString(context, Const.PREF_SORT_MODE)
        sIconPack = getString(context, Const.PREF_ICON_PACK)
        sCategory = getString(context, Const.PREF_CURR_CATEGORY, AnywhereType.DEFAULT_CATEGORY)
        sDefrostMode = getString(context, Const.PREF_DEFROST_MODE, Const.DEFROST_MODE_DSM)
        sCurrentPage = getInt(context, Const.PREF_CURR_PAGE_NUM)
        sDumpInterval = getInt(context, Const.PREF_DUMP_INTERVAL, 1000)
        sAutoDarkModeStart = getLong(context, Const.PREF_AUTO_DARK_MODE_START)
        sAutoDarkModeEnd = getLong(context, Const.PREF_AUTO_DARK_MODE_END)
    }

    val info: CharSequence
        get() {
            val sb = StringBuilder()
                    .append(getInfoLine("Working Mode", sWorkingMode!!.value))
                    .append(getInfoLine("Background Uri", sBackgroundUri))
                    .append(getInfoLine("ActionBar Type", sActionBarType))
                    .append(getInfoLine("Sort Mode", sSortMode))
                    .append(getInfoLine("Icon Pack", sIconPack))
                    .append(getInfoLine("Dark Mode", sDarkMode))
                    .append(getInfoLine("Card Background Mode", sCardBackgroundMode))
                    .append(getInfoLine("Dump Interval", sDumpInterval.toString()))
                    .append(getInfoLine("Current Page", sCurrentPage.toString()))
                    .append(getInfoLine("Defrost Mode", sDefrostMode))
            return Html.fromHtml(sb.toString())
        }

    private fun getInfoLine(infoName: String, infoValue: String?): CharSequence {
        return StringBuilder()
                .append("<b>").append(infoName).append("</b>")
                .append(": ").append(infoValue).append("<br>")
    }

    fun setsIsStreamCardMode(sIsStreamCardMode: Boolean) {
        GlobalValues.sIsStreamCardMode = sIsStreamCardMode
        putBoolean(Utils.getApp(), Const.PREF_STREAM_CARD_MODE, sIsStreamCardMode)
    }

    fun setsIsStreamCardModeSingleLine(sIsStreamCardModeSingleLine: Boolean) {
        GlobalValues.sIsStreamCardModeSingleLine = sIsStreamCardModeSingleLine
        putBoolean(Utils.getApp(), Const.PREF_STREAM_CARD_SINGLE_LINE, sIsStreamCardModeSingleLine)
    }

    fun setsCardBackgroundMode(sCardBackgroundMode: String) {
        GlobalValues.sCardBackgroundMode = sCardBackgroundMode
        putString(Utils.getApp(), Const.PREF_CARD_BACKGROUND, sCardBackgroundMode)
    }

    fun setsWorkingMode(sWorkingMode: String) {
        GlobalValues.sWorkingMode!!.value = sWorkingMode
        putString(Utils.getApp(), Const.PREF_WORKING_MODE, sWorkingMode)
    }

    @JvmStatic
    fun setsActionBarType(sActionBarType: String = "") {
        GlobalValues.sActionBarType = sActionBarType
        putString(Utils.getApp(), Const.PREF_ACTION_BAR_TYPE, sActionBarType)
    }

    fun clearActionBarType() {
        setsActionBarType()
    }

    fun setsDarkMode(sDarkMode: String) {
        GlobalValues.sDarkMode = sDarkMode
        putString(Utils.getApp(), Const.PREF_DARK_MODE, sDarkMode)
    }

    @JvmStatic
    fun setsBackgroundUri(sBackgroundUri: String) {
        GlobalValues.sBackgroundUri = sBackgroundUri
        putString(Utils.getApp(), Const.PREF_CHANGE_BACKGROUND, sBackgroundUri)
    }

    @JvmStatic
    fun setsSortMode(sSortMode: String) {
        GlobalValues.sSortMode = sSortMode
        putString(Utils.getApp(), Const.PREF_SORT_MODE, sSortMode)
    }

    fun setsIconPack(sIconPack: String) {
        GlobalValues.sIconPack = sIconPack
        putString(Utils.getApp(), Const.PREF_ICON_PACK, sIconPack)
    }

    fun setsIsMd2Toolbar(sIsMd2Toolbar: Boolean) {
        GlobalValues.sIsMd2Toolbar = sIsMd2Toolbar
        putBoolean(Utils.getApp(), Const.PREF_MD2_TOOLBAR, false)
    }

    fun setsCategory(sCategory: String, page: Int) {
        GlobalValues.sCategory = sCategory
        sCurrentPage = page
        putString(Utils.getApp(), Const.PREF_CURR_CATEGORY, sCategory)
        putInt(Utils.getApp(), Const.PREF_CURR_PAGE_NUM, page)
    }

    fun setsCategory(sCategory: String) {
        GlobalValues.sCategory = sCategory
        putString(Utils.getApp(), Const.PREF_CURR_CATEGORY, sCategory)
    }

    @JvmStatic
    fun setsIsPages(sIsPages: Boolean) {
        GlobalValues.sIsPages = sIsPages
        putBoolean(Utils.getApp(), Const.PREF_PAGES, sIsPages)
    }

    fun setsIsCollectorPlus(sIsCollectorPlus: Boolean) {
        GlobalValues.sIsCollectorPlus = sIsCollectorPlus
        putBoolean(Utils.getApp(), Const.PREF_COLLECTOR_PLUS, sIsCollectorPlus)
    }

    fun setsDumpInterval(sDumpInterval: Int) {
        GlobalValues.sDumpInterval = sDumpInterval
        putInt(Utils.getApp(), Const.PREF_DUMP_INTERVAL, sDumpInterval)
    }

    val collectorMode: String
        get() = if (sIsCollectorPlus) {
            "Collector+"
        } else {
            "Collector"
        }

    fun setsIsExcludeFromRecent(sIsExcludeFromRecent: Boolean) {
        GlobalValues.sIsExcludeFromRecent = sIsExcludeFromRecent
        putBoolean(Utils.getApp(), Const.PREF_EXCLUDE_FROM_RECENT, sIsExcludeFromRecent)
    }

    fun setsIsShowShellResult(sIsShowShellResult: Boolean) {
        GlobalValues.sIsShowShellResult = sIsShowShellResult
        putBoolean(Utils.getApp(), Const.PREF_SHOW_SHELL_RESULT, sIsShowShellResult)
    }

    fun setsAutoDarkModeStart(sAutoDarkModeStart: Long) {
        GlobalValues.sAutoDarkModeStart = sAutoDarkModeStart
        putLong(Utils.getApp(), Const.PREF_AUTO_DARK_MODE_START, sAutoDarkModeStart)
    }

    fun setsAutoDarkModeEnd(sAutoDarkModeEnd: Long) {
        GlobalValues.sAutoDarkModeEnd = sAutoDarkModeEnd
        putLong(Utils.getApp(), Const.PREF_AUTO_DARK_MODE_END, sAutoDarkModeEnd)
    }

    fun setsDefrostMode(sDefrostMode: String) {
        GlobalValues.sDefrostMode = sDefrostMode
        putString(Utils.getApp(), Const.PREF_DEFROST_MODE, sDefrostMode)
    }

    @JvmStatic
    val workingMode: String
        get() = sWorkingMode?.let {
            if (TextUtils.isEmpty(it.value)) {
                Const.WORKING_MODE_URL_SCHEME
            } else {
                it.value
            }
        } ?: Const.WORKING_MODE_URL_SCHEME
}
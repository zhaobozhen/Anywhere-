package com.absinthe.anywhere_.constants

import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.utils.SPUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

object GlobalValues {

    private val mmkv: MMKV = MMKV.mmkvWithID(SPUtils.sPName)

    var sIsDebugMode = false

    var cardModeLiveData = MutableLiveData<Any>()

    var shortcutsList = listOf<String>()
        get() = try {
            Gson().fromJson<List<String>>(mmkv.decodeString(Const.SHORTCUTS_LIST), object : TypeToken<List<String>>() {}.type)
                    ?: listOf()
        } catch (e: JsonSyntaxException) {
            listOf()
        }
        set(value) {
            field = value
            mmkv.encode(Const.SHORTCUTS_LIST, Gson().toJson(value))
            shortcutListChanged = true
        }
    var shortcutListChanged = false

    var cardMode
        get() = mmkv.decodeString(Const.PREF_CARD_MODE)
        set(value) {
            mmkv.encode(Const.PREF_CARD_MODE, value)
        }

    var isMd2Toolbar
        get() = mmkv.decodeBool(Const.PREF_MD2_TOOLBAR)
        set(value) {
            mmkv.encode(Const.PREF_MD2_TOOLBAR, value)
        }

    var isPages
        get() = mmkv.decodeBool(Const.PREF_PAGES)
        set(value) {
            mmkv.encode(Const.PREF_PAGES, value)
        }

    var isCollectorPlus
        get() = mmkv.decodeBool(Const.PREF_COLLECTOR_PLUS)
        set(value) {
            mmkv.encode(Const.PREF_COLLECTOR_PLUS, value)
        }

    var isExcludeFromRecent
        get() = mmkv.decodeBool(Const.PREF_EXCLUDE_FROM_RECENT)
        set(value) {
            mmkv.encode(Const.PREF_EXCLUDE_FROM_RECENT, value)
        }

    var showShellResultMode
        get() = mmkv.decodeString(Const.PREF_SHOW_SHELL_RESULT_MODE) ?: Const.SHELL_RESULT_TOAST
        set(value) {
            mmkv.encode(Const.PREF_SHOW_SHELL_RESULT_MODE, value)
        }

    var isAutoBackup
        get() = mmkv.decodeBool(Const.PREF_WEBDAV_AUTO_BACKUP, true)
        set(value) {
            mmkv.encode(Const.PREF_WEBDAV_AUTO_BACKUP, value)
        }

    var workingMode
        get() = mmkv.decodeString(Const.PREF_WORKING_MODE, Const.WORKING_MODE_URL_SCHEME)
                ?: Const.WORKING_MODE_URL_SCHEME
        set(value) {
            mmkv.encode(Const.PREF_WORKING_MODE, value)
        }

    var actionBarType
        get() = mmkv.decodeString(Const.PREF_ACTION_BAR_TYPE, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_ACTION_BAR_TYPE, value)
        }

    var darkMode
        get() = mmkv.decodeString(Const.PREF_DARK_MODE, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_DARK_MODE, value)
        }

    var backgroundUri
        get() = mmkv.decodeString(Const.PREF_CHANGE_BACKGROUND, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_CHANGE_BACKGROUND, value)
        }

    var sCardBackgroundMode
        get() = mmkv.decodeString(Const.PREF_CARD_BACKGROUND, "off") ?: "off"
        set(value) {
            mmkv.encode(Const.PREF_CARD_BACKGROUND, value)
        }

    var sortMode
        get() = mmkv.decodeString(Const.PREF_SORT_MODE, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_SORT_MODE, value)
        }

    var iconPack
        get() = mmkv.decodeString(Const.PREF_ICON_PACK, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_ICON_PACK, value)
        }

    var category
        get() = mmkv.decodeString(Const.PREF_CURR_CATEGORY, AnywhereType.Category.DEFAULT_CATEGORY)
                ?: AnywhereType.Category.DEFAULT_CATEGORY
        set(value) {
            mmkv.encode(Const.PREF_CURR_CATEGORY, value)
        }

    var defrostMode
        get() = mmkv.decodeString(Const.PREF_DEFROST_MODE, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_DEFROST_MODE, value)
        }

    var webdavHost
        get() = mmkv.decodeString(Const.PREF_WEBDAV_HOST, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_WEBDAV_HOST, value)
        }

    var webdavUsername
        get() = mmkv.decodeString(Const.PREF_WEBDAV_USERNAME, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_WEBDAV_USERNAME, value)
        }

    var webdavPassword
        get() = mmkv.decodeString(Const.PREF_WEBDAV_PASSWORD, "") ?: ""
        set(value) {
            mmkv.encode(Const.PREF_WEBDAV_PASSWORD, value)
        }

    var currentPage
        get() = mmkv.decodeInt(Const.PREF_CURR_PAGE_NUM, 0)
        set(value) {
            mmkv.encode(Const.PREF_CURR_PAGE_NUM, value)
        }

    var dumpInterval
        get() = mmkv.decodeInt(Const.PREF_DUMP_INTERVAL, 1000)
        set(value) {
            mmkv.encode(Const.PREF_DUMP_INTERVAL, value)
        }

    var autoDarkModeStart
        get() = mmkv.decodeLong(Const.PREF_AUTO_DARK_MODE_START, 0)
        set(value) {
            mmkv.encode(Const.PREF_AUTO_DARK_MODE_START, value)
        }

    var autoDarkModeEnd
        get() = mmkv.decodeLong(Const.PREF_AUTO_DARK_MODE_END, 0)
        set(value) {
            mmkv.encode(Const.PREF_AUTO_DARK_MODE_END, value)
        }

    var needBackup
        get() = mmkv.decodeBool(Const.PREF_NEED_BACKUP, true)
        set(value) {
            mmkv.encode(Const.PREF_NEED_BACKUP, value)
        }

    var editorEntryAnim
        get() = mmkv.decodeBool(Const.PREF_EDITOR_ENTRY_ANIM, true)
        set(value) {
            mmkv.encode(Const.PREF_EDITOR_ENTRY_ANIM, value)
        }

    val info: CharSequence
        get() {
            val sb = StringBuilder()
                    .append(getInfoLine("Working Mode", workingMode))
                    .append(getInfoLine("Background Uri", backgroundUri))
                    .append(getInfoLine("ActionBar Type", actionBarType))
                    .append(getInfoLine("Sort Mode", sortMode))
                    .append(getInfoLine("Icon Pack", iconPack))
                    .append(getInfoLine("Dark Mode", darkMode))
                    .append(getInfoLine("Card Background Mode", sCardBackgroundMode))
                    .append(getInfoLine("Dump Interval", dumpInterval.toString()))
                    .append(getInfoLine("Current Page", currentPage.toString()))
                    .append(getInfoLine("Defrost Mode", defrostMode))
                    .append(getInfoLine("Current Category", category))
            return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

    val collectorMode: String
        get() = if (isCollectorPlus) { "Collector+" } else { "Collector" }

    var tileOneActive
        get() = mmkv.decodeBool(Const.PREF_TILE_ONE_ACTIVE, false)
        set(value) {
            mmkv.encode(Const.PREF_TILE_ONE_ACTIVE, value)
        }
    var tileTwoActive
        get() = mmkv.decodeBool(Const.PREF_TILE_TWO_ACTIVE, false)
        set(value) {
            mmkv.encode(Const.PREF_TILE_TWO_ACTIVE, value)
        }
    var tileThreeActive
        get() = mmkv.decodeBool(Const.PREF_TILE_THREE_ACTIVE, false)
        set(value) {
            mmkv.encode(Const.PREF_TILE_THREE_ACTIVE, value)
        }

    private fun getInfoLine(infoName: String, infoValue: String?): CharSequence {
        return StringBuilder()
                .append("<b>").append(infoName).append("</b>")
                .append(": ").append(infoValue).append("<br>")
    }

    fun clearActionBarType() {
        actionBarType = ""
    }

    fun setsCategory(sCategory: String, page: Int) {
        category = sCategory
        currentPage = page
    }
}
package com.absinthe.anywhere_.utils

import android.content.pm.PackageManager
import android.util.Patterns
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.utils.CipherUtils.encrypt
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler.handleIntent
import com.absinthe.anywhere_.utils.manager.URLManager
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object AppTextUtils {

    /**
     * process and obtain adb result
     *
     * @param result return result
     */
    fun processResultString(result: String): Array<String>? {
        if (!result.contains(" u0 ") || result.indexOf(" u0 ") + 4 >= result.length - 1) {
            return null
        }

        val packageName: String = result.substring(result.indexOf(" u0 ") + 4, result.indexOf("/"))
        val className: String = result.substring(result.indexOf("/") + 1, result.indexOf(" ", result.indexOf("/") + 1))
        return arrayOf(packageName, className)
    }

    /**
     * get launch command of a item
     *
     * @param item the item
     */
    fun getItemCommand(item: AnywhereEntity): String {
        val cmd = StringBuilder()

        when (item.type) {
            AnywhereType.Card.ACTIVITY -> {
                val packageName = item.param1
                var className = item.param2
                val extras: ExtraBean? = try {
                    Gson().fromJson(item.param3, ExtraBean::class.java)
                } catch (e: JsonSyntaxException) {
                    null
                }

                if (className.startsWith(".")) {
                    className = packageName + className
                }
                className = className.replace("\$", "\\$")
                cmd.append(String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, packageName, className))

                extras?.let {
                    cmd.append(" ").append(it.toString())
                }
            }
            AnywhereType.Card.URL_SCHEME -> {
                val urlScheme = item.param1

                if (item.param3.isNotBlank()) {
                    cmd.append(String.format(AnywhereType.Prefix.DYNAMIC_PARAMS_PREFIX_FORMAT, item.param3))
                }
                if (workingMode == Const.WORKING_MODE_URL_SCHEME) {
                    cmd.append(urlScheme)
                } else {
                    cmd.append(String.format(Const.CMD_OPEN_URL_SCHEME_FORMAT, urlScheme))
                }
            }
            AnywhereType.Card.QR_CODE -> {
                cmd.append(AnywhereType.Prefix.QRCODE_PREFIX).append(item.param2)
            }
            AnywhereType.Card.SHELL -> {
                cmd.append(AnywhereType.Prefix.SHELL_PREFIX).append(item.param1)
            }
            AnywhereType.Card.SWITCH_SHELL -> {
                cmd.append(AnywhereType.Prefix.SHELL_PREFIX)

                if (item.param3 == SWITCH_OFF) {
                    cmd.append(item.param1)
                } else if (item.param3 == SWITCH_ON) {
                    cmd.append(item.param2)
                }
            }
            AnywhereType.Card.IMAGE -> {
                cmd.append(AnywhereType.Prefix.IMAGE_PREFIX)
                        .append(item.param1)
            }
        }

        Timber.d(cmd.toString())
        return cmd.toString()
    }

    /**
     * Get current date
     *
     * @return date string
     */
    val currentFormatDate: String
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            return simpleDateFormat.format(date)
        }

    val webDavFormatDate: String
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            return simpleDateFormat.format(date)
        }

    /**
     * Get package name by adb command
     *
     * @param cmd adb command
     * @return package name
     */
    @JvmStatic
    fun getPkgNameByCommand(cmd: String): String? {
        return if (cmd.startsWith("am start -n ")) {
            cmd.removePrefix("am start -n ")

            val splits = cmd.split("/")
            if (splits.size > 1) splits[0] else ""
        } else if (cmd.startsWith("am start -a ")) {
            cmd.removePrefix(Const.CMD_OPEN_URL_SCHEME)
            getPkgNameByUrlScheme(cmd)
        } else {
            null
        }
    }

    /**
     * Get package name by URL Scheme
     *
     * @param url URL Scheme
     * @return package name
     */
    fun getPkgNameByUrlScheme(url: String): String? {
        val resolveInfo = Utils.getApp().packageManager
                .queryIntentActivities(handleIntent(url), PackageManager.MATCH_DEFAULT_ONLY)
        return if (resolveInfo.size != 0) {
            resolveInfo[0].activityInfo.packageName
        } else {
            null
        }
    }

    /**
     * Parse URL from a sharing text
     *
     * @param sharing original text
     * @return URL
     */
    fun parseUrlFromSharingText(sharing: String?): String {
        if (sharing.isNullOrBlank()) {
            return "Error"
        }

        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(sharing)

        return if (matcher.find()) {
            matcher.group().split("\\?".toRegex()).toTypedArray()[0]
        } else {
            ""
        }
    }

    /**
     * Judge that whether the url is an image url
     *
     * @param s url
     * @return true if is an image url
     */
    fun isImageUrl(s: String): Boolean {
        val list = mutableListOf(
                ".jpg", "jpeg", ".png", ".webp", ".gif", ".bmp", ",tif", ".tiff"
        )
        for (suffix in list) {
            if (s.endsWith(suffix)) {
                return true
            }
        }
        return false
    }

    /**
     * Get card sharing URL
     *
     * @param ae Card entity
     * @return URL
     */
    fun genCardSharingUrl(ae: AnywhereEntity?): String {
        val json = Gson().toJson(ae, AnywhereEntity::class.java)
        var encrypted = encrypt(json)

        if (encrypted != null) {
            encrypted = encrypted.replace("\n".toRegex(), "")
        }
        return URLManager.ANYWHERE_SCHEME + URLManager.CARD_SHARING_HOST + "/" + encrypted
    }
}
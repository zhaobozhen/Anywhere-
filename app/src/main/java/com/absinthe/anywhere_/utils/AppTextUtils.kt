package com.absinthe.anywhere_.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.FileUriExposedException
import android.util.Patterns
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.utils.CipherUtils.encrypt
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler.handleIntent
import com.absinthe.anywhere_.utils.manager.URLManager
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
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
    @JvmStatic
    fun getItemCommand(item: AnywhereEntity): String {
        val cmd = StringBuilder()

        when (item.anywhereType) {
            AnywhereType.Card.ACTIVITY -> {
                val packageName = item.param1
                var className = item.param2
                val extras = item.param3
                Timber.d("packageName = %s, className = %s, extras = %s", packageName, className, extras)

                if (className.startsWith(".")) {
                    className = packageName + className
                }
                cmd.append(String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, packageName, className))

                if (extras.isNotBlank()) {
                    val extrasList = extras.split("\n")
                    for (eachLine in extrasList) {
                        cmd.append(" ").append(eachLine)
                    }
                }
            }
            AnywhereType.Card.URL_SCHEME -> {
                val urlScheme = item.param1
                Timber.d("urlScheme = %s", urlScheme)

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
    fun getPkgNameByCommand(cmd: String): String {
        return if (cmd.startsWith("am start -n ")) {
            cmd.removePrefix("am start -n ")

            val splits = cmd.split("/")
            if (splits.size > 1) splits[0] else ""
        } else if (cmd.startsWith("am start -a ")) {
            cmd.removePrefix(Const.CMD_OPEN_URL_SCHEME)
            getPkgNameByUrlScheme(cmd)
        } else {
            ""
        }
    }

    /**
     * Get package name by URL Scheme
     *
     * @param url URL Scheme
     * @return package name
     */
    fun getPkgNameByUrlScheme(url: String): String {
        val resolveInfo = Utils.getApp().packageManager
                .queryIntentActivities(handleIntent(url), PackageManager.MATCH_DEFAULT_ONLY)
        return if (resolveInfo.size != 0) {
            resolveInfo[0].activityInfo.packageName
        } else {
            ""
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
     * Judge that whether it is a gift code
     *
     * @param code code str
     * @return true if is a gift code
     */
    fun isGiftCode(code: String): Boolean {
        return code.matches("^([A-Z0-9]{5}-){3}[A-Z0-9]{5}$".toRegex())
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

    fun processUri(context: Context, uri: Uri) {
        if (uri.host == URLManager.OPEN_HOST) {
            val param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1) ?: ""
            val param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2) ?: ""
            val param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3) ?: ""
            val type = uri.getQueryParameter(Const.INTENT_EXTRA_TYPE) ?: return

            when (type.toInt()) {
                AnywhereType.Card.URL_SCHEME -> {
                    try {
                        URLSchemeHandler.parse(param1, context)
                    } catch (e: Exception) {
                        e.printStackTrace()

                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (AppUtils.atLeastN()) {
                            if (e is FileUriExposedException) {
                                ToastUtil.makeText(R.string.toast_file_uri_exposed)
                            }
                        }
                    }
                }
                AnywhereType.Card.ACTIVITY -> {
                    val ae = AnywhereEntity.Builder().apply {
                        this.param1 = param1
                        this.param2 = param2
                        this.param3 = param3
                        this.type = AnywhereType.Card.ACTIVITY
                    }

                    Opener.with(context).load(ae).open()
                }
                AnywhereType.Card.SHELL -> {
                    val ae = AnywhereEntity.Builder().apply {
                        this.param1 = param1
                        this.type = AnywhereType.Card.SHELL
                    }

                    Opener.with(context).load(ae).open()
                }
            }
        }
    }
}
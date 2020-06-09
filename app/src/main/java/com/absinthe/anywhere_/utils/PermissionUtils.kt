package com.absinthe.anywhere_.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

object PermissionUtils {
    /**
     * Judge that whether device is miui
     */
    val isMIUI: Boolean
        get() = try {
            @SuppressLint("PrivateApi")
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("get", String::class.java)
            val result = get.invoke(c, "ro.miui.ui.version.code") as String

            result.isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    /**
     * Jump to miui permission management activity
     *
     * @param context to launch an activity
     */
    fun goToMIUIPermissionManager(context: Context) {
        try {
            val intent = Intent().apply {
                action = "miui.intent.action.APP_PERM_EDITOR"
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity")
                putExtra("extra_pkgname", context.packageName)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            ToastUtil.makeText("打开失败，请自行前往 MIUI 权限界面")
        }
    }
}
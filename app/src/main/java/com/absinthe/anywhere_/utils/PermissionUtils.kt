package com.absinthe.anywhere_.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.io.DataOutputStream

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

    /**
     * acquire su permission
     *
     * @param pkgCodePath to get su permission of the package
     */
    fun upgradeRootPermission(pkgCodePath: String): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = "chmod 777 $pkgCodePath"
            process = Runtime.getRuntime().exec("su") //change to super user
            os = DataOutputStream(process.outputStream).apply {
                writeBytes("$cmd\n")
                writeBytes("exit\n")
                flush()
            }
            process.waitFor()
        } catch (e: Exception) {
            Timber.d("upgradeRootPermission: %s", e.toString())
            return false
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            return process?.waitFor() == 0
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}
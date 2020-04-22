package com.absinthe.anywhere_.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.FileUriExposedException
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.QRCollection
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler.parse
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.requestShizukuPermission
import com.blankj.utilcode.util.Utils
import moe.shizuku.api.ShizukuService
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object CommandUtils {
    /**
     * execute adb command
     *
     * @param cmd command
     */
    @JvmStatic
    fun execAdbCmd(cmd: String): String {
        val result: String = when (workingMode) {
            Const.WORKING_MODE_SHIZUKU -> execShizukuCmd(cmd)
            Const.WORKING_MODE_ROOT -> execRootCmd(cmd)
            Const.WORKING_MODE_URL_SCHEME -> {
                ToastUtil.makeText(R.string.toast_change_work_mode)
                CommandResult.RESULT_ERROR
            }
            else -> CommandResult.RESULT_ERROR
        }
        Timber.d("execCmd result = %s", result)
        return result
    }

    /**
     * execute adb or intent command
     *
     * @param cmd command
     */
    @JvmStatic
    @SuppressLint("NewApi")
    fun execCmd(cmd: String) {
        var newCmd = cmd
        var result: String

        if (newCmd.startsWith("am start -a")) {
            newCmd = newCmd.replace(Const.CMD_OPEN_URL_SCHEME, "")
            result = try {
                parse(newCmd, Utils.getApp())
                CommandResult.RESULT_SUCCESS
            } catch (e: ActivityNotFoundException) {
                Timber.e(e)
                CommandResult.RESULT_NO_REACT_URL
            } catch (e: FileUriExposedException) {
                Timber.e(e)
                CommandResult.RESULT_FILE_URI_EXPOSED
            }
        } else if (newCmd.startsWith("am start -n")) {
            val pkgClsString = newCmd.replace("am start -n ", "")
            val pkg = pkgClsString.split("/").toTypedArray()[0]
            val cls = pkgClsString.split("/").toTypedArray()[1]

            if (UiUtils.isActivityExported(Utils.getApp(), ComponentName(pkg, cls))) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        component = ComponentName(pkg, cls)
                    }
                    Utils.getApp().startActivity(intent)
                    result = CommandResult.RESULT_SUCCESS
                } catch (e: ActivityNotFoundException) {
                    Timber.d(e)
                    result = CommandResult.RESULT_NO_REACT_URL
                } catch (e: SecurityException) {
                    Timber.d(e)
                    result = CommandResult.RESULT_SECURITY_EXCEPTION
                }
            } else {
                result = execAdbCmd(newCmd)
            }
        } else {
            when {
                newCmd.startsWith(AnywhereType.QRCODE_PREFIX) -> {
                    newCmd = newCmd.replace(AnywhereType.QRCODE_PREFIX, "")
                    QRCollection.Singleton.INSTANCE.instance.getQREntity(newCmd)?.apply {
                        launch()
                    }
                    result = CommandResult.RESULT_SUCCESS
                }
                newCmd.startsWith(AnywhereType.SHELL_PREFIX) -> {
                    newCmd = newCmd.replace(AnywhereType.SHELL_PREFIX, "")
                    execAdbCmd(newCmd)
                    result = CommandResult.RESULT_SUCCESS
                }
                newCmd.contains("://") -> {
                    try {
                        parse(newCmd, Utils.getApp())
                    } catch (e: Exception) {
                        e.printStackTrace()

                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (e is FileUriExposedException) {
                                ToastUtil.makeText(R.string.toast_file_uri_exposed)
                            }
                        }
                    }
                    result = CommandResult.RESULT_URL_SCHEME
                }
                else -> {
                    result = execAdbCmd(newCmd)
                }
            }
        }
        Timber.d("execCmd result = %s", result)

        when (result) {
            CommandResult.RESULT_NO_REACT_URL -> ToastUtil.makeText(R.string.toast_no_react_url)
            CommandResult.RESULT_ROOT_PERM_ERROR -> ToastUtil.makeText(R.string.toast_check_perm)
            CommandResult.RESULT_SHIZUKU_PERM_ERROR -> {
                ToastUtil.makeText(R.string.toast_check_perm)
                try {
                    requestShizukuPermission()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            CommandResult.RESULT_FILE_URI_EXPOSED -> ToastUtil.makeText(R.string.toast_file_uri_exposed)
            CommandResult.RESULT_SECURITY_EXCEPTION -> ToastUtil.makeText(R.string.toast_security_exception)
            else -> {
            }
        }
    }

    /**
     * execute adb or intent command by root
     *
     * @param cmd command
     */
    private fun execRootCmd(cmd: String): String {
        val result = StringBuilder()
        var os: OutputStream? = null
        var `is`: InputStream? = null

        try {
            val p = Runtime.getRuntime().exec("su") // Rooted device has su command
            os = p.outputStream
            `is` = p.inputStream

            Timber.i(cmd)
            os.apply {
                write("$cmd\n".toByteArray())
                flush()
                write("exit\n".toByteArray())
                flush()
            }
            var c: Int
            while (`is`.read().also { c = it } != -1) {
                result.append(c.toChar())
            }
            p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
            result.append(CommandResult.RESULT_ROOT_PERM_ERROR)
        } finally {
            try {
                os?.close()
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (result.toString().isEmpty()) {
            result.append(CommandResult.RESULT_EMPTY)
        }
        return result.toString()
    }

    /**
     * execute adb or intent via shizuku manager
     *
     * @param cmd command
     */
    private fun execShizukuCmd(cmd: String): String {
        Timber.d(cmd)
        val sb = StringBuilder()

        try {
            val remoteProcess = ShizukuService.newProcess(arrayOf("sh"), null, null)
            val `is` = remoteProcess.inputStream
            val os = remoteProcess.outputStream

            os.apply {
                write("$cmd\n".toByteArray())
                write("exit\n".toByteArray())
                close()
            }
            var c: Int
            while (`is`.read().also { c = it } != -1) {
                sb.append(c.toChar())
            }
            `is`.close()

            Timber.d("newProcess: %s", remoteProcess)
            Timber.d("waitFor: %s", remoteProcess.waitFor())
            Timber.d("output: %s", sb)
        } catch (tr: Throwable) {
            Timber.e(tr, "newProcess")
            sb.append(CommandResult.RESULT_SHIZUKU_PERM_ERROR)
        }

        if (sb.toString().isEmpty()) {
            sb.append(CommandResult.RESULT_EMPTY)
        }

        return sb.toString()
    }
}
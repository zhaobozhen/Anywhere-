package com.absinthe.anywhere_.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.FileUriExposedException
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.ShizukuProcess
import com.absinthe.anywhere_.model.SuProcess
import com.absinthe.anywhere_.model.manager.QRCollection
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.requestShizukuPermission
import com.blankj.utilcode.util.Utils
import timber.log.Timber
import java.net.URISyntaxException

object CommandUtils {
    /**
     * execute adb command
     *
     * @param cmd command
     */
    @JvmStatic
    fun execAdbCmd(cmd: String): String {
        return when (workingMode) {
            Const.WORKING_MODE_SHIZUKU -> execShizukuCmd(cmd)
            Const.WORKING_MODE_ROOT -> execRootCmd(cmd)
            Const.WORKING_MODE_URL_SCHEME -> CommandResult.RESULT_CHANGE_WORKING_MODE
            else -> CommandResult.RESULT_ERROR
        }
    }

    /**
     * execute adb or intent command
     *
     * @param cmd command
     */
    @SuppressLint("NewApi")
    fun execCmd(cmd: String) {
        var newCmd = cmd
        var result: String

        if (newCmd.startsWith("am start -a")) {
            newCmd = newCmd.replace(Const.CMD_OPEN_URL_SCHEME, "")
            result = try {
                URLSchemeHandler.parse(Utils.getApp(), newCmd)
                CommandResult.RESULT_SUCCESS
            } catch (e: ActivityNotFoundException) {
                Timber.e(e)
                CommandResult.RESULT_NO_REACT_URL
            } catch (e: FileUriExposedException) {
                Timber.e(e)
                CommandResult.RESULT_FILE_URI_EXPOSED
            } catch (e: RuntimeException) {
                Timber.e(e)
                CommandResult.RESULT_ERROR
            } catch (e: URISyntaxException) {
                Timber.e(e)
                CommandResult.RESULT_NO_REACT_URL
            }
        } else if (newCmd.startsWith("am start -n")) {
            val pkgClsString = newCmd.replace("am start -n ", "")
            val pkg = pkgClsString.split("/")[0]
            val cls = pkgClsString.split("/")[1]

            if (AppUtils.isActivityExported(Utils.getApp(), ComponentName(pkg, cls))) {
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
                } catch (e: RuntimeException) {
                    Timber.e(e)
                    result = CommandResult.RESULT_ERROR
                }
            } else {
                result = execAdbCmd(newCmd)
            }
        } else {
            when {
                newCmd.startsWith(AnywhereType.Prefix.QRCODE_PREFIX) -> {
                    newCmd = newCmd.replace(AnywhereType.Prefix.QRCODE_PREFIX, "")
                    QRCollection.Singleton.INSTANCE.instance.getQREntity(newCmd)?.apply {
                        launch()
                    }
                    result = CommandResult.RESULT_SUCCESS
                }
                newCmd.startsWith(AnywhereType.Prefix.SHELL_PREFIX) -> {
                    newCmd = newCmd.removePrefix(AnywhereType.Prefix.SHELL_PREFIX)
                    execAdbCmd(newCmd)
                    result = CommandResult.RESULT_SUCCESS
                }
                newCmd.contains("://") -> {
                    try {
                        URLSchemeHandler.parse(Utils.getApp(), newCmd)
                    } catch (e: Exception) {
                        e.printStackTrace()

                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (e is RuntimeException) {
                            ToastUtil.makeText(R.string.toast_runtime_error)
                        } else if (AppUtils.atLeastN()) {
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
            CommandResult.RESULT_ERROR -> ToastUtil.makeText(R.string.toast_runtime_error)
            CommandResult.RESULT_CHANGE_WORKING_MODE -> ToastUtil.makeText(R.string.toast_change_work_mode)
        }
    }

    /**
     * execute adb or intent command by root
     *
     * @param cmd command
     */
    private fun execRootCmd(cmd: String): String {
        Timber.i(cmd)
        val result = StringBuilder()

        try {
            result.append(SuProcess.exec(cmd))
        } catch (e: Exception) {
            e.printStackTrace()
            result.append(CommandResult.RESULT_ROOT_PERM_ERROR)
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
            sb.append(ShizukuProcess.exec(cmd))
            Timber.e(sb.toString())
        } catch (e:Exception) {
            Timber.e(e)
            sb.append(CommandResult.RESULT_SHIZUKU_PERM_ERROR)
        }

        if (sb.toString().isEmpty()) {
            sb.append(CommandResult.RESULT_EMPTY)
        }

        return sb.toString()
    }
}
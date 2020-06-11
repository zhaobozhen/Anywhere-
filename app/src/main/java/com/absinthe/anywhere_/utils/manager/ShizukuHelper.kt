package com.absinthe.anywhere_.utils.manager

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.IServiceManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.ActivityStackManager.topActivity
import com.absinthe.anywhere_.utils.manager.DialogManager.showCheckShizukuWorkingDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showGotoShizukuManagerDialog
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.PermissionUtils
import moe.shizuku.api.ShizukuApiConstants
import moe.shizuku.api.ShizukuService
import timber.log.Timber

/**
 * Shizuku Helper
 *
 *
 * Init Shizuku API.
 */
object ShizukuHelper {

//    val SERVICE_MANAGER = IServiceManager.Stub

    private var isShizukuV3Failed = false

    /**
     * check whether shizuku service is on working
     *
     * @param context to show a dialog
     */
    fun checkShizukuOnWorking(context: Context): Boolean {
        // Shizuku v3 service will send binder via Content Provider to this process when this activity comes foreground.

        // Wait a few seconds here for binder
        if (!ShizukuService.pingBinder()) {
            if (isShizukuV3Failed) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                ToastUtil.makeText("Shizuku: provider started with no binder included.")
            }

            // Shizuku v3 may not running, notify user
            Timber.e("Shizuku v3 may not running.")
            showCheckShizukuWorkingDialog(context)
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true
        }
        return false
    }

    val isGrantShizukuPermission: Boolean
        get() = PermissionUtils.isGranted(ShizukuApiConstants.PERMISSION)

    fun requestShizukuPermission() {
        if (isGrantShizukuPermission) {
            return
        }
        topActivity?.let {
            try {
                it.requestPermissions(arrayOf(ShizukuApiConstants.PERMISSION), Const.REQUEST_CODE_SHIZUKU_PERMISSION)
            } catch (e:Exception) {
                showPermissionDialog(it)
            }
        }
    }

    private fun showPermissionDialog(activity: Activity) {
        showGotoShizukuManagerDialog(activity, DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            val intent = IntentUtils.getLaunchAppIntent("moe.shizuku.privileged.api")
            if (intent != null) {
                activity.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION)
            } else {
                ToastUtil.makeText(R.string.toast_not_install_shizuku)
                URLSchemeHandler.parse(URLManager.SHIZUKU_MARKET_URL, activity)
            }
        })
    }
}
package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.dumpInterval
import com.absinthe.anywhere_.constants.GlobalValues.isCollectorPlus
import com.absinthe.anywhere_.model.manager.CollectorWindowManager
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CommandUtils.execAdbCmd
import com.absinthe.anywhere_.utils.ToastUtil
import com.blankj.utilcode.util.PermissionUtils
import timber.log.Timber

class CollectorService : Service() {

    private val binder = object : ICollectorService.Stub() {

        override fun startCollector() {
            startCollectorInternal()
        }

        override fun stopCollector() {
            stopCollectorInternal()
        }
    }
    private val mCollectorWindowManager by lazy { CollectorWindowManager(applicationContext, binder) }

    private val mHandler = Handler(Looper.myLooper()!!)
    private val getCurrentInfoTask: Runnable = object : Runnable {
        override fun run() {
            val result = execAdbCmd(Const.CMD_GET_TOP_STACK_ACTIVITY)

            if (result == CommandResult.RESULT_NULL ||
                    result == CommandResult.RESULT_ROOT_PERM_ERROR ||
                    result == CommandResult.RESULT_SHIZUKU_PERM_ERROR) {
                Thread.currentThread().interrupt()
            } else {
                AppTextUtils.processResultString(result)?.let {
                    mCollectorWindowManager.setInfo(it[0], it[1])
                }
            }

            mHandler.postDelayed(this, dumpInterval.toLong())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        Timber.d("CollectorService onDestroy.")
        mHandler.removeCallbacks(getCurrentInfoTask)
        super.onDestroy()
    }

    private fun startCollectorInternal() {
        if (!PermissionUtils.isGrantedDrawOverlays()) {
            ToastUtil.Toasty.show(this,
                    if (AppUtils.atLeastR()) {
                        R.string.toast_overlay_choose_anywhere
                    } else {
                        R.string.toast_permission_overlap
                    }
            )

            PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    startCollectorImpl()
                }

                override fun onDenied() {}
            })
        } else {
            startCollectorImpl()
        }
    }

    private fun stopCollectorInternal() {
        if (isCollectorPlus) {
            mHandler.removeCallbacks(getCurrentInfoTask)
        }
        mCollectorWindowManager.removeView()
        stopSelf()
    }

    private fun startCollectorImpl() {
        mCollectorWindowManager.addView()

        if (isCollectorPlus) {
            mHandler.post(getCurrentInfoTask)
        }

        Toast.makeText(this, R.string.toast_collector_opened, Toast.LENGTH_SHORT).show()
    }
}
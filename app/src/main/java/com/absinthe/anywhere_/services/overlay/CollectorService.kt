package com.absinthe.anywhere_.services.overlay

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.dumpInterval
import com.absinthe.anywhere_.constants.GlobalValues.isCollectorPlus
import com.absinthe.anywhere_.model.manager.CollectorWindowManager
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CommandUtils.execAdbCmd
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.blankj.utilcode.util.PermissionUtils
import timber.log.Timber

class CollectorService : Service() {

    private lateinit var mCollectorWindowManager: CollectorWindowManager
    private val binder = CollectorBinder()

    @SuppressLint("HandlerLeak")
    private val mHandler = Handler()
    private val getCurrentInfoTask: Runnable = object : Runnable {
        override fun run() {
            val result = execAdbCmd(Const.CMD_GET_TOP_STACK_ACTIVITY)

            if (result == CommandResult.RESULT_NULL ||
                    result == CommandResult.RESULT_ROOT_PERM_ERROR ||
                    result == CommandResult.RESULT_SHIZUKU_PERM_ERROR) {
                Thread.currentThread().interrupt()
            } else {
                TextUtils.processResultString(result)?.let {
                    mCollectorWindowManager.setInfo(it[0], it[1])
                }
            }

            mHandler.postDelayed(this, dumpInterval.toLong())
        }
    }

    override fun onCreate() {
        super.onCreate()
        initCollectorWindowManager()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        Timber.d("CollectorService onDestroy.")
        super.onDestroy()
    }

    fun startCollector() {
        if (!PermissionUtils.isGrantedDrawOverlays()) {
            ToastUtil.makeText(
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

    fun stopCollector() {
        if (isCollectorPlus) {
            mHandler.removeCallbacks(getCurrentInfoTask)
        }
        mCollectorWindowManager.removeView()
        stopSelf()
    }

    private fun initCollectorWindowManager() {
        mCollectorWindowManager = CollectorWindowManager(applicationContext, this)
    }

    private fun startCollectorImpl() {
        mCollectorWindowManager.addView()

        if (isCollectorPlus) {
            mHandler.post(getCurrentInfoTask)
        }

        ToastUtil.makeText(R.string.toast_collector_opened)
    }

    inner class CollectorBinder : Binder() {
        val service: CollectorService
            get() = this@CollectorService
    }
}
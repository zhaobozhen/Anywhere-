package com.absinthe.anywhere_.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.absinthe.anywhere_.model.OverlayWindowManager
import timber.log.Timber

class OverlayService : Service() {

    private lateinit var mWindowManager: OverlayWindowManager
    private val binder: OverlayBinder = OverlayBinder()

    private fun initWindowManager(cmd: String, pkgName: String) {
        mWindowManager = OverlayWindowManager(applicationContext, this, cmd, pkgName)
    }

    fun closeOverlay() {
        mWindowManager.removeView()
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("OverlayService onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val cmdStr = intent.getStringExtra(COMMAND_STR)
        val pkgName = intent.getStringExtra(PKG_NAME)

        if (cmdStr != null && pkgName != null) {
            initWindowManager(cmdStr, pkgName)
        } else {
            stopSelf()
        }
        mWindowManager.addView()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        Timber.d("OverlayService onDestroy.")
        super.onDestroy()
    }

    inner class OverlayBinder : Binder() {
        val service: OverlayService
            get() = this@OverlayService
    }

    companion object {
        const val COMMAND_STR = "COMMAND_STR"
        const val PKG_NAME = "PKG_NAME"
    }
}
package com.absinthe.anywhere_.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.absinthe.anywhere_.model.OverlayWindowManager
import timber.log.Timber

class OverlayService : Service() {

    private var mWindowManager: OverlayWindowManager? = null

    private fun initWindowManager(cmd: String, pkgName: String) {
        if (mWindowManager == null)
            mWindowManager = OverlayWindowManager(applicationContext, cmd, pkgName)
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
        }

        val command = intent.getStringExtra(COMMAND)
        if (command != null) {
            if (command == COMMAND_OPEN) {
                mWindowManager?.addView()
            } else if (command == COMMAND_CLOSE) {
                Timber.d("Intent:COMMAND_CLOSE")
                mWindowManager?.removeView()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Timber.d("OverlayService onDestroy.")
        super.onDestroy()
    }
    
    companion object {
        const val COMMAND = "COMMAND"
        const val COMMAND_STR = "COMMAND_STR"
        const val PKG_NAME = "PKG_NAME"
        const val COMMAND_OPEN = "COMMAND_OPEN"
        const val COMMAND_CLOSE = "COMMAND_CLOSE"
    }
}
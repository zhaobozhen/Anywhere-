package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.OverlayWindowManager
import timber.log.Timber

class OverlayService : Service() {

    private val binder = object : IOverlayService.Stub() {
        override fun closeOverlay() {
            mWindowManager.removeView()
            stopSelf()
        }
    }
    private lateinit var mWindowManager: OverlayWindowManager

    override fun onCreate() {
        super.onCreate()
        Timber.i("OverlayService onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val entity = intent.getParcelableExtra<AnywhereEntity>(ENTITY)

        if (entity != null) {
            mWindowManager = OverlayWindowManager(applicationContext, binder, entity)
            startActivity(Intent(Intent.ACTION_MAIN).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addCategory(Intent.CATEGORY_HOME)
            })
        } else {
            stopSelf()
        }
        mWindowManager.addView()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        Timber.d("OverlayService onDestroy.")
        super.onDestroy()
    }

    companion object {
        const val ENTITY = "ENTITY"
    }
}
package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.OverlayWindowManager
import timber.log.Timber

class OverlayService : Service() {

    private lateinit var mWindowManager: OverlayWindowManager
    private val binder: OverlayBinder = OverlayBinder()

    private fun initWindowManager(entity: AnywhereEntity) {
        mWindowManager = OverlayWindowManager(applicationContext, this, entity)
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
        val entity = intent.getParcelableExtra<AnywhereEntity>(ENTITY)

        if (entity != null) {
            startActivity(Intent(Intent.ACTION_MAIN).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addCategory(Intent.CATEGORY_HOME)
            })
            initWindowManager(entity)
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
        const val ENTITY = "ENTITY"
    }
}
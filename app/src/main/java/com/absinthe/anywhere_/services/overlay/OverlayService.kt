package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.OverlayWindowManager
import timber.log.Timber

class OverlayService : Service() {

  private val binder = object : IOverlayService.Stub() {

    override fun addOverlay(entity: AnywhereEntity?) {
      entity?.let {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
          this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          addCategory(Intent.CATEGORY_HOME)
        })
        windowManager.addView(it)
      }
    }

    override fun closeOverlay(entity: AnywhereEntity?) {
      entity?.let { windowManager.removeView(it) }
      stopSelf()
    }
  }
  private lateinit var windowManager: OverlayWindowManager

  override fun onCreate() {
    super.onCreate()
    Timber.i("OverlayService onCreate")
    windowManager = OverlayWindowManager(applicationContext, binder)
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_NOT_STICKY
  }

  override fun onBind(intent: Intent): IBinder {
    return binder
  }

  override fun onDestroy() {
    Timber.d("OverlayService onDestroy.")
    windowManager.release()
    super.onDestroy()
  }

}

package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.OverlayWindowManager
import timber.log.Timber
import java.lang.ref.WeakReference

class OverlayService : Service() {

  private val binder by lazy { OverlayBinder(WeakReference(this)) }
  private val windowManager by lazy { OverlayWindowManager(applicationContext, binder) }

  override fun onCreate() {
    super.onCreate()
    Timber.i("OverlayService onCreate")
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

  private class OverlayBinder(private val serviceRef: WeakReference<OverlayService>) : IOverlayService.Stub() {
    override fun addOverlay(entity: AnywhereEntity?) {
      entity?.let {
        try {
          serviceRef.get()?.startActivity(Intent(Intent.ACTION_MAIN).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
          })
        } catch (e: Exception) {
          Timber.e(e)
        }
        serviceRef.get()?.windowManager?.addView(it)
      }
    }

    override fun closeOverlay(entity: AnywhereEntity?) {
      entity?.let { serviceRef.get()?.windowManager?.removeView(it) }
      serviceRef.get()?.stopSelf()
    }
  }
}

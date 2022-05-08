package com.absinthe.anywhere_.services.overlay

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Toast
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.dumpInterval
import com.absinthe.anywhere_.constants.GlobalValues.isCollectorPlus
import com.absinthe.anywhere_.model.manager.CollectorWindowManager
import com.absinthe.anywhere_.model.manager.CoordinatorWindowManager
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CommandUtils.execAdbCmd
import com.absinthe.anywhere_.utils.NotifyUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.blankj.utilcode.util.PermissionUtils
import timber.log.Timber
import java.lang.ref.WeakReference

class CollectorService : Service() {

  private val listenerList = RemoteCallbackList<ICollectorListener>()
  private val binder by lazy { CollectorServiceBinder(WeakReference(this)) }
  private val mCollectorWindowManager by lazy { CollectorWindowManager(applicationContext, binder) }
  private val mCoordinatorWindowManager by lazy {
    CoordinatorWindowManager(applicationContext, binder)
  }

  private val mHandler = Handler(Looper.myLooper()!!)
  private val getCurrentInfoTask: Runnable = object : Runnable {
    override fun run() {
      Timber.d("getCurrentInfoTask#run")
      val result = execAdbCmd(Const.CMD_GET_TOP_STACK_ACTIVITY)

      if (result == CommandResult.RESULT_NULL ||
        result == CommandResult.RESULT_ROOT_PERM_ERROR ||
        result == CommandResult.RESULT_SHIZUKU_PERM_ERROR
      ) {
        Thread.currentThread().interrupt()
      } else {
        AppTextUtils.processResultString(result)?.let {
          if (isCollectorPlus) {
            mCollectorWindowManager.setInfo(it[0], it[1])
          }
          NotifyUtils.updateCollectorNotification(this@CollectorService, it[0], it[1])
        }
      }

      mHandler.postDelayed(this, dumpInterval.toLong())
    }
  }

  private var isSendingBroadcast = false
  private var isCollectorRunning = false
  private var isCoordinatorRunning = false

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    return START_NOT_STICKY
  }

  override fun onBind(intent: Intent): IBinder {
    return binder
  }

  override fun onDestroy() {
    Timber.d("CollectorService onDestroy.")
    mHandler.removeCallbacks(getCurrentInfoTask)
    stopCollectorInternal()
    super.onDestroy()
  }

  private fun startCollectorInternal(): Boolean {
    if (!PermissionUtils.isGrantedDrawOverlays()) {
      ToastUtil.Toasty.show(
        this,
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
      return false
    } else {
      startCollectorImpl()
      return true
    }
  }

  private fun stopCollectorInternal() {
    if (isCollectorRunning) {
      mHandler.removeCallbacks(getCurrentInfoTask)
      mCollectorWindowManager.removeView()
      NotifyUtils.cancelCollectorNotification(this)
      isCollectorRunning = false
    }
    stopSelf()

    if (serviceConnection != null) {
      applicationContext.unbindService(serviceConnection!!)
      serviceConnection = null
    }
  }

  private fun startCollectorImpl() {
    if (isCollectorRunning) {
      return
    }
    isCollectorRunning = true
    mCollectorWindowManager.addView()
    mHandler.post(getCurrentInfoTask)

    NotifyUtils.createCollectorNotification(this)

    Toast.makeText(this, R.string.toast_collector_opened, Toast.LENGTH_SHORT).show()
  }

  private fun startCoordinatorInternal(): Boolean {
    if (!PermissionUtils.isGrantedDrawOverlays()) {
      ToastUtil.Toasty.show(
        this,
        if (AppUtils.atLeastR()) {
          R.string.toast_overlay_choose_anywhere
        } else {
          R.string.toast_permission_overlap
        }
      )

      PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
        override fun onGranted() {
          startCoordinatorImpl()
        }

        override fun onDenied() {}
      })
      return false
    } else {
      startCoordinatorImpl()
      return true
    }
  }

  private fun startCoordinatorImpl() {
    if (isCoordinatorRunning) {
      return
    }
    isCoordinatorRunning = true
    mCoordinatorWindowManager.addView()
  }

  private fun stopCoordinatorInternal(x: Int, y: Int) {
    if (isCoordinatorRunning) {
      notifyCoordinatorSelected(x, y)
      mCoordinatorWindowManager.removeView()
      isCoordinatorRunning = false
    }
    stopSelf()
  }

  private fun notifyCoordinatorSelected(x: Int, y: Int) {
    if (!isSendingBroadcast) {
      isSendingBroadcast = true
      Timber.i("notifyFinished start")
      val count = listenerList.beginBroadcast()
      for (i in 0 until count) {
        try {
          Timber.i("notifyFinished $i")
          listenerList.getBroadcastItem(i).onCoordinatorSelected(x, y)
        } catch (e: RemoteException) {
          Timber.e(e)
        }
      }
      listenerList.finishBroadcast()
      isSendingBroadcast = false
    }
  }

  private class CollectorServiceBinder(private val serviceRef: WeakReference<CollectorService>) : ICollectorService.Stub() {
    override fun startCollector() {
      serviceRef.get()?.startCollectorInternal()
    }

    override fun stopCollector() {
      serviceRef.get()?.stopCollectorInternal()
    }

    override fun startCoordinator() {
      serviceRef.get()?.startCoordinatorInternal()
    }

    override fun stopCoordinator(x: Int, y: Int) {
      serviceRef.get()?.stopCoordinatorInternal(x, y)
    }

    override fun registerCollectorListener(listener: ICollectorListener?) {
      listener?.let { serviceRef.get()?.listenerList?.register(listener) }
    }

    override fun unregisterCollectorListener(listener: ICollectorListener?) {
      serviceRef.get()?.listenerList?.unregister(listener)
    }
  }

  companion object {
    var serviceConnection: ServiceConnection? = null
  }
}

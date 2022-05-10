package com.absinthe.anywhere_

import android.os.Handler
import android.os.Looper
import android.util.Log
import timber.log.Timber

object Global {

  private val handler = Handler(Looper.getMainLooper())

  fun start() {
    handler.post {
      while (true) {
        try {
          Looper.loop()
        } catch (e: Throwable) {
          dealStackTraceException(e)
        }
      }
    }
  }

  @Throws(Throwable::class)
  private fun dealStackTraceException(e: Throwable) {
    val stack = Log.getStackTraceString(e)

    if (stack.contains("Service.startForeground()") ||
      stack.contains("com.swift.sandhook") ||
      stack.contains("updateForceDarkMode") ||
      stack.contains("MultiSelectPopupWindow.showMultiSelectPopupWindow") ||
      stack.contains("get life cycle exception") ||
      stack.contains("checkStartAnyActivityPermission")
    ) {
      Timber.w(e)
    } else if (stack.contains("ClipboardService")) {
      Timber.w(e)
    } else if (stack.contains("de.robv.android.xposed")) {
      Timber.w(e)
    } else {
      throw e
    }
  }
}

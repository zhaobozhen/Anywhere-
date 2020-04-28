package com.absinthe.anywhere_.model

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.absinthe.anywhere_.view.CollectorView
import timber.log.Timber

class CollectorWindowManager(private val context: Context) {

    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    var view: CollectorView? = null
        private set

    fun addView() {
        view ?: run {
            view = CollectorView(context).apply {
                layoutParams = LAYOUT_PARAMS
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }
            mWindowManager.addView(view, LAYOUT_PARAMS)
            Timber.d("Collector addView.")
        }
    }

    fun removeView() {
        view?.let {
            mWindowManager.removeView(view)
            view = null
            Timber.d("Collector removeView.")
        }
    }

    fun setInfo(pkgName: String, clsName: String) {
        view?.setInfo(pkgName, clsName)
    }

    companion object {
        var LAYOUT_PARAMS = WindowManager.LayoutParams().apply {
            x = width
            y = height / 2
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
    }
}
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
        if (view == null) {
            view = CollectorView(context)
            view!!.layoutParams = LAYOUT_PARAMS
            view!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            mWindowManager.addView(view, LAYOUT_PARAMS)
            Timber.d("Collector addView.")
        }
    }

    fun removeView() {
        if (view != null) {
            mWindowManager.removeView(view)
            view = null
            Timber.d("Collector removeView.")
        }
    }

    fun setInfo(pkgName: String, clsName: String) {
        if (view != null) {
            view!!.setInfo(pkgName, clsName)
        }
    }

    companion object {
        private var LAYOUT_PARAMS: WindowManager.LayoutParams? = null

        init {
            val params = WindowManager.LayoutParams()
            params.x = params.width
            params.y = params.height / 2
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_PHONE
            }
            params.format = PixelFormat.RGBA_8888
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            LAYOUT_PARAMS = params
        }
    }
}
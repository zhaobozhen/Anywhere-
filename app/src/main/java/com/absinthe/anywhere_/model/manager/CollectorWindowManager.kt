package com.absinthe.anywhere_.model.manager

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.absinthe.anywhere_.services.overlay.ICollectorService
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.view.home.CollectorView
import timber.log.Timber

class CollectorWindowManager(context: Context, binder: ICollectorService) {

    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var hasAdded = false

    var view: CollectorView = CollectorView(context, binder)
        private set

    fun addView() {
        if (!hasAdded) {
            view.apply {
                layoutParams = LAYOUT_PARAMS
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }

            mWindowManager.addView(view, LAYOUT_PARAMS)
        }
        hasAdded = true
        Timber.d("Collector addView.")
    }

    fun removeView() {
        if (hasAdded) {
            view.let {
                mWindowManager.removeView(view)
                Timber.d("Collector removeView.")
            }
        }
        hasAdded = false
    }

    fun setInfo(pkgName: String, clsName: String) {
        view.setInfo(pkgName, clsName)
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

            type = if (AppUtils.atLeastO()) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
    }
}
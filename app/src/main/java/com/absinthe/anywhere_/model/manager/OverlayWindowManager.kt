package com.absinthe.anywhere_.model.manager

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.services.overlay.IOverlayService
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.view.home.OverlayView
import timber.log.Timber

class OverlayWindowManager(context: Context, binder: IOverlayService, entity: AnywhereEntity) {

    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mEntity = entity
    private var mOverlayView: OverlayView = OverlayView(context, binder)
    private var hasAdded = false

    fun addView() {
        if (!hasAdded) {
            mOverlayView.apply {
                entity = mEntity
                layoutParams = LAYOUT_PARAMS
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }

            mWindowManager.addView(mOverlayView, LAYOUT_PARAMS)
            Timber.d("Overlay window addView.")
        }
        hasAdded = true
    }

    fun removeView() {
        if (hasAdded) {
            mWindowManager.removeView(mOverlayView)
            Timber.d("Overlay window removeView.")
        }
        hasAdded = false
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
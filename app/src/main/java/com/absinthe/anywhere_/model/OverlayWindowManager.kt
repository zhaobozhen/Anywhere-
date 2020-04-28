package com.absinthe.anywhere_.model

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.absinthe.anywhere_.view.OverlayView
import timber.log.Timber

class OverlayWindowManager(private val mContext: Context, cmd: String, pkgName: String) {

    private val mWindowManager: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mCommand: String = cmd
    private val mPkgName: String = pkgName
    private var mOverlayView: OverlayView? = null

    fun addView() {
        if (mOverlayView == null) {
            mOverlayView = OverlayView(mContext).apply {
                command = mCommand
                pkgName = mPkgName
                layoutParams = LAYOUT_PARAMS
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }

            mWindowManager.addView(mOverlayView, LAYOUT_PARAMS)
            Timber.d("Overlay window addView.")
        }
    }

    fun removeView() {
        if (mOverlayView != null) {
            mWindowManager.removeView(mOverlayView)
            mOverlayView = null
            Timber.d("Overlay window removeView.")
        }
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
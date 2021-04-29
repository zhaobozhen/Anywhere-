package com.absinthe.anywhere_.model.manager

import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import com.absinthe.anywhere_.services.overlay.ICollectorService
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.view.home.CoordinatorView
import timber.log.Timber

class CoordinatorWindowManager(context: Context, private val binder: ICollectorService) {
    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var hasAdded = false

    var view: CoordinatorView = CoordinatorView(context)
        private set

    fun addView() {
        if (!hasAdded) {
            mWindowManager.addView(view, LAYOUT_PARAMS)
            view.confirmView.setOnClickListener {
                binder.stopCoordinator(view.targetView.x.toInt(), view.targetView.y.toInt())
            }
        }
        hasAdded = true
        Timber.d("Coordinator addView.")
    }

    fun removeView() {
        if (hasAdded) {
            view.let {
                mWindowManager.removeView(view)
                Timber.d("Coordinator removeView.")
            }
        }
        hasAdded = false
    }

    companion object {
        var LAYOUT_PARAMS = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            format = PixelFormat.RGBA_8888

            type = if (AppUtils.atLeastO()) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
    }
}
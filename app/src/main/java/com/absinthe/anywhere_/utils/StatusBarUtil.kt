package com.absinthe.anywhere_.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.absinthe.libraries.utils.extensions.dp
import com.blankj.utilcode.util.Utils

object StatusBarUtil {
    /**
     * Clear light status bar state
     *
     * @param view decor view
     */
    @JvmStatic
    fun clearLightStatusBarAndNavigationBar(view: View) {
        view.systemUiVisibility.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            view.systemUiVisibility.and(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv())
        }
    }

    fun setSystemBarStyle(activity: Activity, needLightStatusBar: Boolean = true) {
        val decorView = activity.window.decorView
        if (isDarkMode(activity)) {
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        } else {
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            if (needLightStatusBar) {
                decorView.systemUiVisibility =
                        decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            if (getNavBarHeight() > 20.dp) {
                if (AppUtils.atLeastO()) {
                    decorView.systemUiVisibility = (
                            decorView.systemUiVisibility
                                    or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }
            }
        }
        setSystemBarTransparent(activity)
    }

    fun getNavBarHeight(): Int {
        //Full screen adaption
        if (Settings.Global.getInt(Utils.getApp().contentResolver, "force_fsg_nav_bar", 0) != 0) {
            return 20.dp
        }

        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    /**
     * Judge that whether is dark mode
     *
     * @param context context
     * @return true if is dark mode
     */
    fun isDarkMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    fun setSystemBarTransparent(activity: Activity) {
        val window = activity.window
        val view = window.decorView
        val flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        view.systemUiVisibility = view.systemUiVisibility or flag

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
    }
}
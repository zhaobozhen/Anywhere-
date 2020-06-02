package com.absinthe.anywhere_.utils

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.ConvertUtils
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

    fun setDarkMode(activity: Activity, isDarkMode: Boolean) {
        if (isDarkMode) {
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        } else {
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            if (getNavBarHeight() > ConvertUtils.dp2px(20f)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.window.decorView.systemUiVisibility = (
                            activity.window.decorView.systemUiVisibility
                                    or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }
            }
        }
    }

    fun getNavBarHeight(): Int {
        //Full screen adaption
        if (Settings.Global.getInt(Utils.getApp().contentResolver, "force_fsg_nav_bar", 0) != 0) {
            return ConvertUtils.dp2px(20f)
        }

        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
}
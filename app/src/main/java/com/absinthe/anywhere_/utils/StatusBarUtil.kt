package com.absinthe.anywhere_.utils

import android.os.Build
import android.view.View

object StatusBarUtil {
    /**
     * Clear light status bar state
     *
     * @param view decor view
     */
    fun clearLightStatusBarAndNavigationBar(view: View) {
        view.systemUiVisibility.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            view.systemUiVisibility.and(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv())
        }
    }
}
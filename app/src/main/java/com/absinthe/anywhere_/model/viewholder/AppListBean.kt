package com.absinthe.anywhere_.model.viewholder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

data class AppListBean(
        val id: String,
        val appName: String = "",
        val packageName: String = "",
        val className: String = "",
        val icon: Drawable = ColorDrawable(Color.TRANSPARENT),
        val type: Int,
        val isExported: Boolean = false,
        var isLaunchActivity: Boolean = false
)
package com.absinthe.anywhere_.model.viewholder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.absinthe.anywhere_.constants.AnywhereType

data class AppListBean(
        var appName: String = "",
        var packageName: String = "",
        var className: String = "",
        var icon: Drawable = ColorDrawable(Color.TRANSPARENT),
        var type: Int = AnywhereType.Card.URL_SCHEME,
        var isExported: Boolean = false
)
package com.absinthe.anywhere_.model.viewholder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.absinthe.anywhere_.constants.AnywhereType

class AppListBean {

    var appName: String
    var packageName: String
    var className: String
    var icon: Drawable
    var type: Int

    constructor() {
        appName = ""
        packageName = ""
        className = ""
        icon = ColorDrawable(Color.TRANSPARENT)
        type = AnywhereType.URL_SCHEME
    }

    constructor(appName: String, packageName: String, className: String, type: Int) {
        this.appName = appName
        this.packageName = packageName
        this.className = className
        this.icon = ColorDrawable(Color.TRANSPARENT)
        this.type = type
    }

    constructor(appName: String, packageName: String, className: String, type: Int, icon: Drawable) {
        this.appName = appName
        this.packageName = packageName
        this.className = className
        this.icon = icon
        this.type = type
    }

}
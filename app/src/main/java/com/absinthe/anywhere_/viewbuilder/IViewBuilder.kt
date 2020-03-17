package com.absinthe.anywhere_.viewbuilder

import android.view.View

internal interface IViewBuilder {
    fun init()
    fun addView(view: View)
    fun removeView(view: View)
}
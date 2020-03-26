package com.absinthe.anywhere_.viewbuilder

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup

/**
 * View Builder
 *
 *
 * To build a view with Java code.
 */
open class ViewBuilder : IViewBuilder {

    lateinit var root: ViewGroup
        protected set
    protected var mContext: Context

    protected constructor(context: Context) {
        mContext = context
    }

    protected constructor(context: Context, viewGroup: ViewGroup) {
        mContext = context
        root = viewGroup
    }

    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

    override fun addView(view: View) {
        root.addView(view)
    }

    override fun removeView(view: View) {
        root.removeView(view)
    }
}
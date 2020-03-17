package com.absinthe.anywhere_.viewbuilder

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.absinthe.anywhere_.utils.UiUtils

/**
 * View Builder
 *
 *
 * To build a view with Java code.
 */
abstract class ViewBuilder : IViewBuilder {

    lateinit var root: ViewGroup
        protected set
    @JvmField
    protected var mContext: Context
    @JvmField
    protected var mResources: Resources

    protected constructor(context: Context) {
        mContext = context
        mResources = mContext.resources
        initImpl()
    }

    protected constructor(context: Context, viewGroup: ViewGroup) {
        mContext = context
        mResources = mContext.resources
        root = viewGroup
        initImpl()
    }

    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

    abstract override fun init()

    override fun addView(view: View) {
        root.addView(view)
    }

    override fun removeView(view: View) {
        root.removeView(view)
    }

    protected fun d2p(dipValue: Float): Int {
        return UiUtils.d2p(mContext, dipValue)
    }

    private fun initImpl() {
        init()
    }

    protected class Params {
        object LL {
            @JvmField
            val WRAP_WRAP = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            val WRAP_MATCH = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            @JvmField
            val MATCH_WRAP = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            @JvmField
            val MATCH_MATCH = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)

            @JvmStatic
            fun customParams(width: Int, height: Int): LinearLayout.LayoutParams {
                return LinearLayout.LayoutParams(width, height)
            }
        }
    }
}
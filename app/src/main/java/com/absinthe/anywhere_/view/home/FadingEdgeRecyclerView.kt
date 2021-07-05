package com.absinthe.anywhere_.view.home

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class FadingEdgeRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun isPaddingOffsetRequired(): Boolean {
        return !clipToPadding
    }

    override fun getLeftPaddingOffset(): Int {
        return if (clipToPadding) 0 else -paddingLeft
    }

    override fun getTopPaddingOffset(): Int {
        return if (clipToPadding) 0 else -paddingTop
    }

    override fun getRightPaddingOffset(): Int {
        return if (clipToPadding) 0 else paddingRight
    }

    override fun getBottomPaddingOffset(): Int {
        return if (clipToPadding) 0 else paddingBottom
    }

    override fun getBottomFadingEdgeStrength(): Float {
        return 0f
    }

    override fun hasOverlappingRendering(): Boolean {
        return true
    }

    override fun onSetAlpha(alpha: Int): Boolean {
        return false
    }
}
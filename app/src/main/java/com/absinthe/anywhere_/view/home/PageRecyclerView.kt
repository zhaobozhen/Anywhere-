package com.absinthe.anywhere_.view.home

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.pow

class PageRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var lastX = 0f
    private var lastY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = (ev.x - lastX).absoluteValue
                val deltaY = (ev.y - lastY).absoluteValue

                //如果不能向右滑动或者在进行垂直滑动则不拦截父布局点击事件
                if (!canScrollHorizontally(1) ||
                        ((deltaX.pow(2) + deltaY.pow(2) > touchSlop.toFloat().pow(2)) && (deltaY > deltaX * 4))) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
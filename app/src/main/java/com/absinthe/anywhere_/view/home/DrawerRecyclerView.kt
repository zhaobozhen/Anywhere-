package com.absinthe.anywhere_.view.home

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.pow

class DrawerRecyclerView : RecyclerView {

    var isEditMode = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var lastX = 0f
    private var lastY = 0f

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (isEditMode) return super.onInterceptTouchEvent(e)

        var intercepted = false

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                lastX = e.x
                lastY = e.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = (e.x - lastX).absoluteValue
                val deltaY = (e.y - lastY).absoluteValue

                //若垂直滑动则拦截点击事件
                intercepted = (deltaX.pow(2) + deltaY.pow(2) > touchSlop.toFloat().pow(2)) && (deltaY > deltaX * 4)
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }

        return intercepted
    }
}
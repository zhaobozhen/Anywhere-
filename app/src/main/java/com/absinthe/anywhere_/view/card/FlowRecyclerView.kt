package com.absinthe.anywhere_.view.card

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class FlowRecyclerView : RecyclerView {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
    context,
    attrs,
    defStyle
  )

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
        //如果不能垂直滑动则不拦截父布局点击事件
        if (!canScrollVertically(1) || !canScrollVertically(-1)) {
          parent.requestDisallowInterceptTouchEvent(false)
        }
      }
    }

    return super.dispatchTouchEvent(ev)
  }
}

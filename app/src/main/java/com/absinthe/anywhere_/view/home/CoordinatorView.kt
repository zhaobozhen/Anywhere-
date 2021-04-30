package com.absinthe.anywhere_.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.absinthe.anywhere_.R
import com.absinthe.libchecker.view.AViewGroup
import timber.log.Timber

class CoordinatorView(context: Context) : AViewGroup(context) {

    //Last x, y position = 0f
    private var lastX = 0f
    private var lastY = 0f

    //Current x, y position = 0f
    private var nowX = 0f
    private var nowY = 0f

    //悬浮窗移动位置的相对值 = 0f
    private var tranX = 0f
    private var tranY = 0f

    val confirmView = Button(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text = context.getString(R.string.dialog_delete_positive_button)
        elevation = 5.dp.toFloat()
    }

    val targetView = AppCompatImageView(context).apply {
        layoutParams = LayoutParams(24.dp, 24.dp)
        setImageResource(R.drawable.ic_target)
    }

    init {
        addView(confirmView)
        addView(targetView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        confirmView.autoMeasure()
        targetView.autoMeasure()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        confirmView.layout(confirmView.toHorizontalCenter(this), measuredHeight - 100.dp)
        targetView.layout(targetView.toHorizontalCenter(this), targetView.toVerticalCenter(this))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when(motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // 获取按下时的X，Y坐标
                lastX = motionEvent.rawX
                lastY = motionEvent.rawY
                Timber.d("MotionEvent.ACTION_DOWN last: %f %f", lastX, lastY)
            }
            MotionEvent.ACTION_MOVE -> {
                // 获取移动时的X，Y坐标
                nowX = motionEvent.rawX
                nowY = motionEvent.rawY
                Timber.d("MotionEvent.ACTION_MOVE now: %f %f", nowX, nowY)

                // 计算XY坐标偏移量
                tranX = nowX - lastX
                tranY = nowY - lastY
                Timber.d("MotionEvent.ACTION_MOVE tran: %f %f", tranX, tranY)

                // 移动悬浮窗
                targetView.apply {
                    x = (x + tranX.toInt()).coerceAtLeast(0f).coerceAtMost((this@CoordinatorView.measuredWidth - measuredWidth).toFloat())
                    y = (y + tranY.toInt()).coerceAtLeast(0f).coerceAtMost((this@CoordinatorView.measuredHeight - measuredHeight).toFloat())
                }
                //记录当前坐标作为下一次计算的上一次移动的位置坐标
                lastX = nowX
                lastY = nowY
                
                if (targetView.y > measuredHeight / 2) {
                    confirmView.y = 100.dp.toFloat()
                } else {
                    confirmView.y = (measuredHeight - 100.dp).toFloat()
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return super.onTouchEvent(motionEvent)
    }
}

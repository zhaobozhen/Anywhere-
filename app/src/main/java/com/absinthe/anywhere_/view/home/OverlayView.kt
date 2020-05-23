package com.absinthe.anywhere_.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.LinearLayout
import com.absinthe.anywhere_.model.manager.OverlayWindowManager
import com.absinthe.anywhere_.services.overlay.OverlayService
import com.absinthe.anywhere_.utils.CommandUtils
import com.absinthe.anywhere_.viewbuilder.entity.OverlayBuilder
import com.blankj.utilcode.util.AppUtils
import timber.log.Timber

@SuppressLint("ViewConstructor")
class OverlayView(context: Context, private val service: OverlayService) : LinearLayout(context) {

    var command: String = ""
    var pkgName: String = ""
        set(value) {
            field = value
            mBuilder.ivIcon.setImageDrawable(AppUtils.getAppIcon(field))
        }

    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private val mLayoutParams = OverlayWindowManager.LAYOUT_PARAMS

    private lateinit var mBuilder: OverlayBuilder
    private var isClick = false
    private var mStartTime: Long = 0
    private var mEndTime: Long = 0

    private val removeWindowTask = Runnable {
        mBuilder.ivIcon.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        service.closeOverlay()
    }

    init {
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        mBuilder = OverlayBuilder(context, this)

        mBuilder.ivIcon.setOnClickListener {
            Timber.d("Overlay window clicked!")
            CommandUtils.execCmd(command)
        }
        mBuilder.ivIcon.setOnTouchListener(object : OnTouchListener {
            //Last x, y position = 0f
            private var lastX = 0f
            private var lastY = 0f

            //Current x, y position = 0f
            private var nowX = 0f
            private var nowY = 0f

            //悬浮窗移动位置的相对值 = 0f
            private var tranX = 0f
            private var tranY = 0f

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 获取按下时的X，Y坐标
                        lastX = motionEvent.rawX
                        lastY = motionEvent.rawY
                        Timber.d("MotionEvent.ACTION_DOWN last: %f %f", lastX, lastY)
                        isClick = false
                        mStartTime = System.currentTimeMillis()
                        postDelayed(removeWindowTask, 1000)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        isClick = true

                        // 获取移动时的X，Y坐标
                        nowX = motionEvent.rawX
                        nowY = motionEvent.rawY
                        Timber.d("MotionEvent.ACTION_MOVE now: %f %f", nowX, nowY)

                        // 计算XY坐标偏移量
                        tranX = nowX - lastX
                        tranY = nowY - lastY
                        Timber.d("MotionEvent.ACTION_MOVE tran: %f %f", tranX, tranY)
                        if (tranX * tranX + tranY * tranY > mTouchSlop * mTouchSlop) {
                            removeCallbacks(removeWindowTask)
                        }

                        // 移动悬浮窗
                        mLayoutParams.apply {
                            x -= tranX.toInt()
                            y += tranY.toInt()
                        }
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(this@OverlayView, mLayoutParams)
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX
                        lastY = nowY
                    }
                    MotionEvent.ACTION_UP -> {
                        mEndTime = System.currentTimeMillis()
                        Timber.d("Touch period = %d", mEndTime - mStartTime)
                        isClick = mEndTime - mStartTime > 0.2 * 1000L
                        removeCallbacks(removeWindowTask)
                    }
                }
                return isClick
            }
        })
    }
}
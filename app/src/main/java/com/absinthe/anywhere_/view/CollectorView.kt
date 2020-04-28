package com.absinthe.anywhere_.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.CollectorWindowManager
import com.absinthe.anywhere_.services.CollectorService
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CommandUtils
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.viewbuilder.entity.CollectorBuilder
import timber.log.Timber

class CollectorView(context: Context) : LinearLayout(context) {

    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mLayoutParams: WindowManager.LayoutParams = CollectorWindowManager.LAYOUT_PARAMS

    private var mBuilder: CollectorBuilder = CollectorBuilder(context, this)
    private var mPackageName: String = ""
    private var mClassName: String = ""
    private var isClick = false
    private var mStartTime: Long = 0
    private var mEndTime: Long = 0


    init {
        initView()
    }

    fun setInfo(pkgName: String, clsName: String) {
        if (GlobalValues.isCollectorPlus) {
            mBuilder.tvPkgName.text = pkgName
            mBuilder.tvClsName.text = clsName
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        mBuilder.ibCollector.setOnClickListener {
            Timber.d("Collector clicked!")
            collectActivity()
            CollectorService.closeCollector(context)
            AppUtils.openUrl(context, mPackageName, mClassName, "")
        }
        mBuilder.ibCollector.setOnTouchListener(object : OnTouchListener {
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

                        // 移动悬浮窗
                        mLayoutParams.apply {
                            x -= tranX.toInt()
                            y += tranY.toInt()
                        }
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(this@CollectorView, mLayoutParams)
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX
                        lastY = nowY
                    }
                    MotionEvent.ACTION_UP -> {
                        mEndTime = System.currentTimeMillis()
                        Timber.d("Touch period = %d", mEndTime - mStartTime)
                        isClick = mEndTime - mStartTime > 0.2 * 1000L
                    }
                }
                return isClick
            }
        })
    }

    private fun collectActivity() {
        val cmd = Const.CMD_GET_TOP_STACK_ACTIVITY
        val result = CommandUtils.execAdbCmd(cmd)
        Timber.d("Shell result = %s", result)

        if (result == CommandResult.RESULT_SHIZUKU_PERM_ERROR
                || result == CommandResult.RESULT_ROOT_PERM_ERROR
                || result == CommandResult.RESULT_ERROR) {
            ToastUtil.makeText(R.string.toast_check_perm)
        } else {
            val processed = TextUtils.processResultString(result)
            if (processed != null) {
                mPackageName = processed[0]
                mClassName = processed[1]
            }
        }
    }
}
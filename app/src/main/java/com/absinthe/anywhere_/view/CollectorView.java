package com.absinthe.anywhere_.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.viewbuilder.CollectorBuilder;

public class CollectorView extends LinearLayout {

    private final Context mContext;
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private String mPackageName, mClassName;

    private boolean isClick;
    private long mStartTime = 0;
    private long mEndTime = 0;

    public CollectorView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        CollectorBuilder builder = new CollectorBuilder(mContext, this);

        builder.ibCollector.setOnClickListener(v -> {
            Logger.d("Collector clicked!");
            collectActivity();
            mContext.startService(
                    new Intent(mContext, CollectorService.class)
                            .putExtra(CollectorService.COMMAND, CollectorService.COMMAND_CLOSE)
            );

            AppUtils.openUrl(mContext, mPackageName, mClassName, "");
        });

        builder.ibCollector.setOnTouchListener(new OnTouchListener() {

            private float lastX; //上一次位置的X.Y坐标
            private float lastY;
            private float nowX;  //当前移动位置的X.Y坐标
            private float nowY;
            private float tranX; //悬浮窗移动位置的相对值
            private float tranY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mLayoutParams = (WindowManager.LayoutParams) CollectorView.this.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                        Logger.d("MotionEvent.ACTION_DOWN last:", lastX, lastY);

                        isClick = false;
                        mStartTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isClick = true;

                        // 获取移动时的X，Y坐标
                        nowX = motionEvent.getRawX();
                        nowY = motionEvent.getRawY();
                        Logger.d("MotionEvent.ACTION_MOVE now:", nowX, nowY);

                        // 计算XY坐标偏移量
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        Logger.d("MotionEvent.ACTION_MOVE tran:", tranX, tranY);

                        // 移动悬浮窗
                        mLayoutParams.x -= tranX;
                        mLayoutParams.y += tranY;
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(CollectorView.this, mLayoutParams);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;

                        break;
                    case MotionEvent.ACTION_UP:
                        mEndTime = System.currentTimeMillis();
                        Logger.d("Touch period =", (mEndTime - mStartTime));
                        isClick = (mEndTime - mStartTime) > 0.2 * 1000L;
                        break;
                }
                return isClick;
            }
        });
    }

    private void collectActivity() {
        String cmd = Const.CMD_GET_TOP_STACK_ACTIVITY;
        String result = CommandUtils.execAdbCmd(cmd);

        Logger.d("Shell result =", result);

        if (result != null) {
            String[] processed = TextUtils.processResultString(result);

            if (processed != null) {
                mPackageName = processed[0];
                mClassName = processed[1];
            }
        } else {
            ToastUtil.makeText(R.string.toast_check_perm);
        }
    }

}

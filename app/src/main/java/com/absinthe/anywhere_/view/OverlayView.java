package com.absinthe.anywhere_.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.services.OverlayService;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.UiUtils;

public class OverlayView extends LinearLayout {

    private static final int MSG_REMOVE_WINDOW = 1001;

    private final Context mContext;
    private final WindowManager mWindowManager;
    private final int mTouchSlop;

    private WindowManager.LayoutParams mLayoutParams;
    private ImageButton ibIcon;

    private String mCommand;
    private String mPkgName;

    private boolean isClick;
    private long mStartTime = 0;
    private long mEndTime = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_REMOVE_WINDOW) {
                ibIcon.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mContext.startService(
                        new Intent(mContext, OverlayService.class)
                                .putExtra(OverlayService.COMMAND, OverlayService.COMMAND_CLOSE)
                );
            }
        }
    };

    public OverlayView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        int width = UiUtils.dipToPixels(mContext, 65);
        int height = UiUtils.dipToPixels(mContext, 65);
        setLayoutParams(new LinearLayout.LayoutParams(width, height));

        ibIcon = new ImageButton(mContext);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(width, height);
        ibIcon.setLayoutParams(layoutParams);
        ibIcon.setBackground(null);
        addView(ibIcon);

        ibIcon.setOnClickListener(v -> {
            Logger.d("Overlay window clicked!");

            CommandUtils.execCmd(mCommand);
        });

        ibIcon.setOnTouchListener(new OnTouchListener() {

            private float lastX; //上一次位置的X.Y坐标
            private float lastY;
            private float nowX;  //当前移动位置的X.Y坐标
            private float nowY;
            private float tranX; //悬浮窗移动位置的相对值
            private float tranY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mLayoutParams = (WindowManager.LayoutParams) OverlayView.this.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                        Logger.d("MotionEvent.ACTION_DOWN last:", lastX, lastY);

                        isClick = false;
                        mStartTime = System.currentTimeMillis();
                        mHandler.sendEmptyMessageDelayed(MSG_REMOVE_WINDOW, 1000);
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

                        if (tranX * tranX + tranY * tranY > mTouchSlop * mTouchSlop) {
                            mHandler.removeMessages(MSG_REMOVE_WINDOW);
                        }

                        // 移动悬浮窗
                        mLayoutParams.x -= tranX;
                        mLayoutParams.y += tranY;
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(OverlayView.this, mLayoutParams);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;

                        break;
                    case MotionEvent.ACTION_UP:
                        mEndTime = System.currentTimeMillis();
                        Logger.d("Touch period =", (mEndTime - mStartTime));

                        isClick = (mEndTime - mStartTime) > 0.2 * 1000L;
                        mHandler.removeMessages(MSG_REMOVE_WINDOW);
                        break;
                }
                return isClick;
            }
        });
    }

    public String getCommand() {
        return mCommand;
    }

    public void setCommand(String mCommand) {
        this.mCommand = mCommand;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
        ibIcon.setImageDrawable(UiUtils.getAppIconByPackageName(mContext, mPkgName));
    }
}


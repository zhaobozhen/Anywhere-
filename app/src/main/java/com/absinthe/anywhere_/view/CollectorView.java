package com.absinthe.anywhere_.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.TextUtils;

public class CollectorView extends LinearLayout {
    public static final String TAG = "CollectorView";

    private final Context mContext;
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;

    private String packageName, className;
    private int classNameType;

    private boolean isClick;
    private long startTime = 0;
    private long endTime = 0;

    public CollectorView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        inflate(mContext, R.layout.layout_collector, this);
        ImageView mIbCollector = findViewById(R.id.ib_collector);

        mIbCollector.setOnClickListener(v -> {
            Log.d(TAG, "Collector clicked!");
            collectActivity();
            mContext.startService(
                    new Intent(mContext, CollectorService.class)
                            .putExtra(CollectorService.COMMAND, CollectorService.COMMAND_CLOSE)
            );
            mContext.startActivity(
                    new Intent(mContext, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(ConstUtil.INTENT_EXTRA_PACKAGE_NAME, packageName)
                            .putExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME, className)
                            .putExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME_TYPE, classNameType));
        });

        mIbCollector.setOnTouchListener(new OnTouchListener() {

            private float lastX; //上一次位置的X.Y坐标
            private float lastY;
            private float nowX;  //当前移动位置的X.Y坐标
            private float nowY;
            private float tranX; //悬浮窗移动位置的相对值
            private float tranY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutParams = (WindowManager.LayoutParams) CollectorView.this.getLayoutParams();
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                        Log.d(TAG, "MotionEvent.ACTION_DOWN last:" + lastX + ", " + lastY);

                        isClick = false;
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isClick = true;

                        // 获取移动时的X，Y坐标
                        nowX = motionEvent.getRawX();
                        nowY = motionEvent.getRawY();
                        Log.d(TAG, "MotionEvent.ACTION_MOVE now:" + nowX + ", " + nowY);

                        // 计算XY坐标偏移量
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        Log.d(TAG, "MotionEvent.ACTION_MOVE tran:" + tranX + ", " + tranY);

                        // 移动悬浮窗
                        layoutParams.x -= tranX;
                        layoutParams.y += tranY;
                        //更新悬浮窗位置
                        mWindowManager.updateViewLayout(CollectorView.this, layoutParams);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;

                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        isClick = (endTime - startTime) > 0.1 * 1000L;
                        break;
                }
                return isClick;
            }
        });
    }

    private void collectActivity() {
        String cmd = ConstUtil.CMD_GET_TOP_STACK_ACTIVITY;
        String result = PermissionUtil.execCmd(cmd);

        Log.d(TAG, "Shell result = " + result);

        if (result != null) {
            String[] processed = TextUtils.processResultString(result);
            packageName = processed[0];
            className = processed[1];
            classNameType = Integer.valueOf(processed[2]);
            Log.d(TAG, "classNameType = " + classNameType);
        }
    }

}

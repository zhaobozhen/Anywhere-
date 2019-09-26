package com.absinthe.anywhere_.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.TextUtils;

public class CollectorView extends LinearLayout {
    public static final String TAG = "CollectorView";

    private final Context mContext;
    private final WindowManager mWindowManager;

    private String packageName, className;
    private int classNameType;

    public CollectorView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

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
    }

    private void collectActivity() {
        String cmd = ConstUtil.CMD_GET_TOP_STACK_ACTIVITY;
        String workingMode = AnywhereApplication.workingMode;
        String result = null;

        if (workingMode.equals(ConstUtil.WORKING_MODE_ROOT)) {
            result = PermissionUtil.execRootCmd(cmd);
        } else if (workingMode.equals(ConstUtil.WORKING_MODE_SHIZUKU)) {
            result = PermissionUtil.execShizukuCmd(cmd);
        } else {
            Log.d(TAG, "workingMode abnormal.");
        }

        Log.d(TAG, "Shell result = " + result);

        if (result != null) {
            String[] processed = TextUtils.processResultString(result);
            packageName = processed[0];
            className = processed[1];
            classNameType = Integer.valueOf(processed[2]);
            Log.d(TAG, "classNameType = " + classNameType);
        }
    }

    Point preP, curP;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                preP = new Point((int)event.getRawX(), (int)event.getRawY());
                performClick();
                break;

            case MotionEvent.ACTION_MOVE:
                curP = new Point((int)event.getRawX(), (int)event.getRawY());
                int dx = curP.x - preP.x,
                        dy = curP.y - preP.y;

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.x += dx;
                layoutParams.y += dy;
                mWindowManager.updateViewLayout(this, layoutParams);

                preP = curP;
                break;
        }

        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}

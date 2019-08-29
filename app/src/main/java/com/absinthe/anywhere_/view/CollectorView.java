package com.absinthe.anywhere_.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.MainActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.PermissionUtil;

public class CollectorView extends LinearLayout {
    public static final String TAG = "CollectorView";

    private final Context mContext;
    private final WindowManager mWindowManager;

    private String packageName, className;

    public CollectorView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_collector, this);
        ImageButton mIbCollector = findViewById(R.id.ib_collector);

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
                            .putExtra("packageName", packageName)
                            .putExtra("className", className));
        });
    }

    private void collectActivity() {
        String cmd = "dumpsys activity activities | grep mResumedActivity";
        String result = PermissionUtil.execShizukuCmd(cmd);
        Log.d(TAG, "Shell result = " + result);

        if (result != null) {
            String[] processed = processResultString(result);
            packageName = processed[0];
            className = processed[1];
        }
    }

    private String[] processResultString(String result) {
        String packageName, className;

        packageName = result.substring(result.indexOf(" u0 ") + 4, result.indexOf("/"));
        className = result.substring(result.indexOf("/") + 1, result.lastIndexOf(" "));
        Log.d(TAG, "packageName = " + packageName);
        Log.d(TAG, "className = " + className);

        return new String[]{packageName, className};
    }

    Point preP, curP;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                preP = new Point((int)event.getRawX(), (int)event.getRawY());
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
}

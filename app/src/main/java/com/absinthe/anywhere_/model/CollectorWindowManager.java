package com.absinthe.anywhere_.model;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.absinthe.anywhere_.view.CollectorView;

public class CollectorWindowManager {
    private final Context mContext;
    private final WindowManager mWindowManager;

    public CollectorWindowManager(Context context) {
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    private View mFloatingView;
    private static final WindowManager.LayoutParams LAYOUT_PARAMS;

    static {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.x = 0;
        params.y = 0;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        LAYOUT_PARAMS = params;
    }

    public void addView() {
        if(mFloatingView == null){
            mFloatingView = new CollectorView(mContext);
            mFloatingView.setLayoutParams(LAYOUT_PARAMS);
            mFloatingView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);

            mWindowManager.addView(mFloatingView, LAYOUT_PARAMS);
        }
    }

    public void removeView(){
        if(mFloatingView != null){
            mWindowManager.removeView(mFloatingView);
            mFloatingView = null;
        }
    }
}

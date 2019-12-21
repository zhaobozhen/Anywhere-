package com.absinthe.anywhere_.model;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.view.OverlayView;

public class OverlayWindowManager {
    private static final WindowManager.LayoutParams LAYOUT_PARAMS;

    private final Context mContext;
    private final WindowManager mWindowManager;

    private OverlayView mOverlayView;
    private String mCommand;
    private String mPkgName;

    static {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.x = params.width;
        params.y = params.height / 2;
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

    public OverlayWindowManager(Context context, String cmd, String pkgName) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mCommand = cmd;
        mPkgName = pkgName;
    }

    public void addView() {
        if (mOverlayView == null) {
            mOverlayView = new OverlayView(mContext);
            mOverlayView.setCommand(mCommand);
            mOverlayView.setPkgName(mPkgName);
            mOverlayView.setLayoutParams(LAYOUT_PARAMS);
            mOverlayView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            mWindowManager.addView(mOverlayView, LAYOUT_PARAMS);
            Logger.d("Overlay window addView.");
        }
    }

    public void removeView() {
        if (mOverlayView != null) {
            mWindowManager.removeView(mOverlayView);
            mOverlayView = null;
            Logger.d("Overlay window removeView.");
        }
    }
}

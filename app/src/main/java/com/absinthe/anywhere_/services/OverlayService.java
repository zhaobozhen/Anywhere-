package com.absinthe.anywhere_.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.OverlayWindowManager;
import com.absinthe.anywhere_.utils.ToastUtil;

import timber.log.Timber;

public class OverlayService extends Service {
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_STR = "COMMAND_STR";
    public static final String PKG_NAME = "PKG_NAME";

    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";

    OverlayWindowManager mWindowManager;

    private void initWindowManager(String cmd, String pkgName) {
        if (mWindowManager == null)
            mWindowManager = new OverlayWindowManager(getApplicationContext(), cmd, pkgName);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.i("OverlayService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            ToastUtil.makeText(R.string.toast_collector_service_launch_failed);
            stopSelf();
        } else {
            String cmdStr = intent.getStringExtra(COMMAND_STR);
            String pkgName = intent.getStringExtra(PKG_NAME);
            if (cmdStr != null && pkgName != null) {
                initWindowManager(cmdStr, pkgName);
            }
            String command = intent.getStringExtra(COMMAND);
            if (command != null) {
                if (command.equals(COMMAND_OPEN)) {
                    mWindowManager.addView();
                } else if (command.equals(COMMAND_CLOSE)) {
                    Timber.d("Intent:COMMAND_CLOSE");
                    mWindowManager.removeView();
                    stopSelf();
                }
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Timber.d("OverlayService onDestroy.");
        super.onDestroy();
    }
}

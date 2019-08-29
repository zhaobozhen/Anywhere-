package com.absinthe.anywhere_.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.model.CollectorWindowManager;

public class CollectorService extends Service {
    private static final String TAG = "CollectorService";
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";

    CollectorWindowManager mCollectorWindowManager;

    private void initCollectorWindowManager() {
        if (mCollectorWindowManager == null)
            mCollectorWindowManager = new CollectorWindowManager(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"CollectorService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initCollectorWindowManager();
        String command = intent.getStringExtra(COMMAND);
        if (command != null) {
            if (command.equals(COMMAND_OPEN)) {
                mCollectorWindowManager.addView();
            }
            else if (command.equals(COMMAND_CLOSE)) {
                Log.d(TAG, "Intent:COMMAND_CLOSE");
                mCollectorWindowManager.removeView();
                stopSelf();
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
        Log.d(TAG, "CollectorService onDestroy.");
        super.onDestroy();
    }
}

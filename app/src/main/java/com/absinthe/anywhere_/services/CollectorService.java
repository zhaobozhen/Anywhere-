package com.absinthe.anywhere_.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.Tile;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.CollectorWindowManager;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.ToastUtil;

public class CollectorService extends Service {
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
        LogUtil.i(this.getClass(), "CollectorService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            ToastUtil.makeText(R.string.toast_collector_service_launch_failed);
            stopSelf();
        } else {
            initCollectorWindowManager();
            String command = intent.getStringExtra(COMMAND);
            if (command != null) {
                if (command.equals(COMMAND_OPEN)) {
                    mCollectorWindowManager.addView();
                } else if (command.equals(COMMAND_CLOSE)) {
                    LogUtil.d(this.getClass(), "Intent:COMMAND_CLOSE");
                    mCollectorWindowManager.removeView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (CollectorTileService.getInstance() != null) {
                            Tile tile = CollectorTileService.getInstance().getQsTile();
                            tile.setState(Tile.STATE_INACTIVE);
                            tile.setLabel(getString(R.string.tile_collector_on));
                            tile.updateTile();
                        }
                    }
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
        LogUtil.d(this.getClass(), "CollectorService onDestroy.");
        super.onDestroy();
    }
}

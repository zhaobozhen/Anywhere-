package com.absinthe.anywhere_.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.quicksettings.Tile;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.CollectorWindowManager;
import com.absinthe.anywhere_.model.CommandResult;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.TextUtils;
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

    private boolean isStart = false;
    private Runnable getCurrentInfoTask = new Runnable() {
        @Override
        public void run() {
            int interval = GlobalValues.sDumpInterval;
            while (isStart && mCollectorWindowManager.getView() != null) {
                try {
                    String result = CommandUtils.execAdbCmd(Const.CMD_GET_TOP_STACK_ACTIVITY);

                    if (result.equals(CommandResult.RESULT_NULL)
                            || result.equals(CommandResult.RESULT_ROOT_PERM_ERROR)
                            || result.equals(CommandResult.RESULT_SHIZUKU_PERM_ERROR)) {
                        isStart = false;
                        Thread.currentThread().interrupt();
                    } else {
                        String[] params = TextUtils.processResultString(result);
                        if (params != null) {
                            String pkgName = params[0];
                            String clsName = params[1];
                            new Handler(Looper.getMainLooper()).post(() ->
                                    mCollectorWindowManager.getView().setInfo(pkgName, clsName));
                        }
                    }

                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("CollectorService onCreate");
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

                    if (GlobalValues.sIsCollectorPlus) {
                        isStart = true;
                        new Thread(getCurrentInfoTask).start();
                    }
                } else if (command.equals(COMMAND_CLOSE)) {
                    Logger.d("Intent:COMMAND_CLOSE");
                    mCollectorWindowManager.removeView();
                    isStart = false;

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
        Logger.d("CollectorService onDestroy.");
        super.onDestroy();
    }
}

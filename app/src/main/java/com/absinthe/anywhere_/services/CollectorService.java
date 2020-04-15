package com.absinthe.anywhere_.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.CollectorWindowManager;
import com.absinthe.anywhere_.constants.CommandResult;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.NotifyUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PermissionUtils;

import timber.log.Timber;

public class CollectorService extends Service {
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";

    CollectorWindowManager mCollectorWindowManager;

    private void initCollectorWindowManager() {
        if (mCollectorWindowManager == null)
            mCollectorWindowManager = new CollectorWindowManager(getApplicationContext());
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler();

    private Runnable getCurrentInfoTask = new Runnable() {
        @Override
        public void run() {
            if (mCollectorWindowManager != null && mCollectorWindowManager.getView() != null) {
                String result = CommandUtils.execAdbCmd(Const.CMD_GET_TOP_STACK_ACTIVITY);

                if (result.equals(CommandResult.RESULT_NULL)
                        || result.equals(CommandResult.RESULT_ROOT_PERM_ERROR)
                        || result.equals(CommandResult.RESULT_SHIZUKU_PERM_ERROR)) {
                    Thread.currentThread().interrupt();
                } else {
                    String[] params = TextUtils.processResultString(result);
                    if (params != null) {
                        mCollectorWindowManager.setInfo(params[0], params[1]);
                    }
                }
            }

            mHandler.postDelayed(this, GlobalValues.sDumpInterval);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NotifyUtils.COLLECTOR_NOTIFICATION_ID, getNotificationInstance());
        Timber.i("CollectorService onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        startForeground(NotifyUtils.COLLECTOR_NOTIFICATION_ID, getNotificationInstance());
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
                        mHandler.post(getCurrentInfoTask);
                    }
                } else if (command.equals(COMMAND_CLOSE)) {
                    Timber.d("Intent:COMMAND_CLOSE");
                    mHandler.removeCallbacks(getCurrentInfoTask);
                    mCollectorWindowManager.removeView();
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
        Timber.d("CollectorService onDestroy.");
        super.onDestroy();
    }

    public static void startCollector(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startCollectorImpl(context);
        } else {
            if (!PermissionUtils.isGrantedDrawOverlays()) {
                if (Build.VERSION.SDK_INT >= 30) {
                    ToastUtil.makeText(R.string.toast_overlay_choose_anywhere);
                } else {
                    ToastUtil.makeText(R.string.toast_permission_overlap);
                }
                PermissionUtils.requestDrawOverlays(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        startCollectorImpl(context);
                    }

                    @Override
                    public void onDenied() {

                    }
                });
            } else {
                startCollectorImpl(context);
            }
        }
    }

    private static void startCollectorImpl(Context context) {
        Intent intent = new Intent(context, CollectorService.class);
        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotifyUtils.createCollectorChannel(context);
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        ToastUtil.makeText(R.string.toast_collector_opened);
        ActivityUtils.startHomeActivity();
    }

    public static void closeCollector(Context context) {
        Intent intent = new Intent(context, CollectorService.class);
        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_CLOSE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotifyUtils.createCollectorChannel(context);
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private Notification getNotificationInstance() {
        return new NotificationCompat.Builder(this, NotifyUtils.COLLECTOR_CHANNEL_ID)
                .setContentTitle(getText(R.string.notification_collector_title))
                .setContentText(getText(R.string.notification_collector_content))
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}

package com.absinthe.anywhere_.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.model.CollectorWindowManager;

import org.greenrobot.eventbus.EventBus;

public class CollectorService extends AccessibilityService {
    private static final String TAG = "CollectorService";
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";

    //要引用的布局文件.
    LinearLayout collectorLayout;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;

    ImageButton imageButton;
    CollectorWindowManager mCollectorWindowManager;

    private void initCollectorWindowManager() {
        if (mCollectorWindowManager == null)
            mCollectorWindowManager = new CollectorWindowManager(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"CollectorService Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initCollectorWindowManager();
        String command = intent.getStringExtra(COMMAND);
        if (command != null) {
            if (command.equals(COMMAND_OPEN))
                mCollectorWindowManager.addView();
            else if (command.equals(COMMAND_CLOSE))
                mCollectorWindowManager.removeView();
                stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: " + event.getPackageName());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            CharSequence packageName = event.getPackageName();
            CharSequence className = event.getClassName();
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)) {
                EventBus.getDefault().post(new ActivityChangedEvent(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                ));
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class ActivityChangedEvent {
        private final String mPackageName;
        private final String mClassName;

        ActivityChangedEvent(String packageName, String className) {
            mPackageName = packageName;
            mClassName = className;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public String getClassName() {
            return mClassName;
        }
    }

}

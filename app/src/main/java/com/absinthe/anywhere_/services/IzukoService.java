package com.absinthe.anywhere_.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.absinthe.anywhere_.a11y.A11yActionBean;
import com.absinthe.anywhere_.a11y.A11yEntity;
import com.absinthe.anywhere_.a11y.A11yType;

import timber.log.Timber;

public class IzukoService extends BaseAccessibilityService {

    @SuppressLint("StaticFieldLeak")
    @Nullable
    private static IzukoService sInstance;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private A11yEntity mA11yEntity = new A11yEntity();
    private String currentActionActivity;
    private boolean isExecutingFinish = false;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sInstance = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sInstance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (isExecutingFinish) {
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            if (TextUtils.equals(event.getPackageName(), mA11yEntity.getApplicationId())) {
                CharSequence className = event.getClassName();

                if (TextUtils.equals(className, currentActionActivity)) {
                    start();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Nullable
    public static IzukoService getInstance() {
        return sInstance;
    }

    public void setA11yEntity(@NonNull A11yEntity a11yEntity) {
        mA11yEntity = a11yEntity;
        currentActionActivity = mA11yEntity.getEntryActivity();
        isExecutingFinish = false;
    }

    private void start() {
        Thread workingThread = new Thread(() -> {
            for (A11yActionBean action : mA11yEntity.getActions()) {
                if (!action.getActionActivity().isEmpty()) {
                    currentActionActivity = action.getActionActivity();
                }
                try {
                    Thread.sleep(action.getDelay());
                    mHandler.post(getActionRunnable(action));
                } catch (InterruptedException e) {
                    Timber.e(e);
                }
            }
            isExecutingFinish = true;
        });
        workingThread.start();
    }

    private Runnable getActionRunnable(@NonNull A11yActionBean actionBean) {
        long startTime = System.currentTimeMillis();

        String[] split = actionBean.getContent().split("\\|");
        String content = null;

        if (actionBean.getType() == A11yType.TEXT || actionBean.getType() == A11yType.LONG_PRESS_TEXT) {
            content = findViewByText(split);
            if (content == null) {
                while (System.currentTimeMillis() - startTime < 5000) {
                    content = findViewByText(split);
                    if (content != null) {
                        break;
                    }
                }
            }
        } else if (actionBean.getType() == A11yType.VIEW_ID || actionBean.getType() == A11yType.LONG_PRESS_VIEW_ID) {
            content = findViewByID(split);
            if (content == null) {
                while (System.currentTimeMillis() - startTime < 5000) {
                    content = findViewByID(split);
                    if (content != null) {
                        break;
                    }
                }
            }
        }

        String finalContent = content;
        return () -> {
            switch (actionBean.getType()) {
                case A11yType.TEXT:
                    clickTextViewByText(finalContent);
                    break;
                case A11yType.VIEW_ID:
                    clickTextViewByID(finalContent);
                    break;
                case A11yType.LONG_PRESS_TEXT:
                    longClickTextViewByText(finalContent);
                    break;
                case A11yType.LONG_PRESS_VIEW_ID:
                    longClickTextViewByID(finalContent);
                    break;
                default:
            }
        };
    }
}

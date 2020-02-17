package com.absinthe.anywhere_.services;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.absinthe.anywhere_.workflow.WorkFlow;

public class IzukoService extends BaseAccessibilityService {
    @SuppressLint("StaticFieldLeak")
    public static IzukoService sInstance;

    private static WorkFlow sWorkFlow = null;
    private static boolean isClicked = true;
    private static String sPackageName = "";
    private static String sClassName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @Override
    public void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (isClicked) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                TextUtils.equals(event.getPackageName(), sPackageName)) {

            CharSequence className = event.getClassName();
            if (className.equals(sClassName)) {
                sWorkFlow.start();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    public static void isClicked(boolean isClicked) {
        IzukoService.isClicked = isClicked;
    }

    public static void setPackageName(String sPackageName) {
        IzukoService.sPackageName = sPackageName;
    }

    public static void setClassName(String sClassName) {
        IzukoService.sClassName = sClassName;
    }

    public static void setWorkFlow(WorkFlow sWorkFlow) {
        IzukoService.sWorkFlow = sWorkFlow;
    }
}

package com.absinthe.anywhere_.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.absinthe.anywhere_.workflow.WorkFlow;

public class IzukoService extends BaseAccessibilityService {
    @SuppressLint("StaticFieldLeak")
    private static IzukoService sInstance;
    private WorkFlow mWorkFlow = new WorkFlow();
    private String mPackageName = "";
    private String mClassName = "";
    private boolean isClicked = true;

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
        if (isClicked) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                TextUtils.equals(event.getPackageName(), mPackageName)) {

            CharSequence className = event.getClassName();

            if (className.equals(mClassName)) {
                mWorkFlow.start();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    public static IzukoService getInstance() {
        return sInstance;
    }

    public void isClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public void setWorkFlow(WorkFlow workFlow) {
        mWorkFlow = workFlow;
    }
}

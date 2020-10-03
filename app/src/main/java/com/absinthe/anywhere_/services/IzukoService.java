package com.absinthe.anywhere_.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.a11y.A11yWorkFlow;

public class IzukoService extends BaseAccessibilityService {
    @SuppressLint("StaticFieldLeak")
    private static IzukoService sInstance;
    private A11yWorkFlow mA11yWorkFlow = new A11yWorkFlow();
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
                mA11yWorkFlow.start();
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

    public void isClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public void setWorkFlow(A11yWorkFlow a11yWorkFlow) {
        mA11yWorkFlow = a11yWorkFlow;
    }
}

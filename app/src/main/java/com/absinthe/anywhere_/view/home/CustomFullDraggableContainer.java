package com.absinthe.anywhere_.view.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.drakeet.drawer.FullDraggableContainer;

public class CustomFullDraggableContainer extends FullDraggableContainer {

    private boolean mShouldEnableDrawer = false;

    public CustomFullDraggableContainer(@NonNull Context context) {
        super(context);
    }

    public CustomFullDraggableContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFullDraggableContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEnableDrawer(boolean shouldEnableDrawer) {
        mShouldEnableDrawer = shouldEnableDrawer;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShouldEnableDrawer) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}

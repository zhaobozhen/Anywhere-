package com.absinthe.anywhere_.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ViewUtils;

public class RoundLinerLayoutNormal extends LinearLayout {
    public RoundLinerLayoutNormal(Context context) {
        super(context);
        initBackground();
    }

    public RoundLinerLayoutNormal(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBackground();
    }

    public RoundLinerLayoutNormal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackground();
    }

    private void initBackground() {
        setBackground(ViewUtils.generateBackgroundWithShadow(this, R.color.navigationColorNormal,
                R.dimen.toolbar_radius_corner,
                R.color.shadow,
                R.dimen.toolbar_elevation,
                Gravity.CENTER));
    }

    public void setCustomBackground(Drawable drawable) {
        setBackground(ViewUtils.generateBackgroundWithShadow(this, drawable,
                R.dimen.toolbar_radius_corner,
                R.color.shadow,
                R.dimen.toolbar_elevation,
                Gravity.CENTER));
    }
}

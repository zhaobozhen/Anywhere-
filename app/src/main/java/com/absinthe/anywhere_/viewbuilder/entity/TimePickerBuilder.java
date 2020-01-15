package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;
import com.google.android.material.button.MaterialButton;

public class TimePickerBuilder extends ViewBuilder {

    public MaterialButton btnStart, btnEnd;

    public TimePickerBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_MATCH);
        ((LinearLayout) root).setOrientation(LinearLayout.HORIZONTAL);
        int padding = d2p(10);
        root.setPadding(padding, padding, padding, padding);
        ((LinearLayout) root).setHorizontalGravity(Gravity.CENTER);
        root.setClipToPadding(false);

        LinearLayout.LayoutParams wrapWrap = Params.LL.WRAP_WRAP;
        btnStart = new MaterialButton(mContext);
        btnStart.setLayoutParams(wrapWrap);
        btnStart.setTextColor(mResources.getColor(R.color.textColorNormal));
        btnStart.setBackgroundColor(mResources.getColor(R.color.navigationColorNormal));
        addView(btnStart);

        TextView tvTo = new TextView(mContext);
        tvTo.setLayoutParams(wrapWrap);
        tvTo.setPadding(d2p(20), 0, d2p(20), 0);
        tvTo.setText("To");
        addView(tvTo);

        btnEnd = new MaterialButton(mContext);
        btnEnd.setLayoutParams(wrapWrap);
        btnEnd.setTextColor(mResources.getColor(R.color.textColorNormal));
        btnEnd.setBackgroundColor(mResources.getColor(R.color.navigationColorNormal));
        addView(btnEnd);
    }
}

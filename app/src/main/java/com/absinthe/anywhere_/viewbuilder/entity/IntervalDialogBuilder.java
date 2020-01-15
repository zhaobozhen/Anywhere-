package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;
import com.google.android.material.slider.Slider;

public class IntervalDialogBuilder extends ViewBuilder {

    public Slider slider;

    public IntervalDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_MATCH);
        int padding = d2p(10);
        root.setPadding(padding, padding, padding, padding);

        slider = new Slider(mContext);
        slider.setLayoutParams(Params.LL.MATCH_WRAP);
        slider.setValueFrom(0.5f);
        slider.setValueTo(2.5f);
        slider.setStepSize(0.25f);
        slider.setLabelFormatter(value -> value + "s");
        addView(slider);
    }
}

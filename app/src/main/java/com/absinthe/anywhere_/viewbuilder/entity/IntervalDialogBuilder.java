package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.view.ViewGroup;
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
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        int padding = d2p(10);
        root.setPadding(padding, padding, padding, padding);

        slider = new Slider(mContext);
        slider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        slider.setValueFrom(0.5f);
        slider.setValueTo(2.5f);
        slider.setStepSize(0.25f);
        slider.setLabelFormatter(value -> value + "s");
        addView(slider);
    }
}

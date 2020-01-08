package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class OverlayBuilder extends ViewBuilder {

    public ImageButton ibIcon;

    public OverlayBuilder(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
    }

    @Override
    public void init() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dipToPixels(65),
                dipToPixels(65));

        root.setLayoutParams(layoutParams);

        ibIcon = new ImageButton(mContext);
        ibIcon.setLayoutParams(layoutParams);
        ibIcon.setBackground(null);

        addView(ibIcon);
    }
}

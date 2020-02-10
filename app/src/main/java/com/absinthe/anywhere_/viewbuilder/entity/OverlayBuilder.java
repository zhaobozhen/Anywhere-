package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class OverlayBuilder extends ViewBuilder {

    public ImageView ivIcon;

    public OverlayBuilder(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
    }

    @Override
    public void init() {
        LinearLayout.LayoutParams layoutParams =
                Params.LL.customParams(d2p(65), d2p(65));

        root.setLayoutParams(layoutParams);

        ivIcon = new ImageView(mContext);
        ivIcon.setLayoutParams(layoutParams);
        ivIcon.setBackground(null);

        addView(ivIcon);
    }
}

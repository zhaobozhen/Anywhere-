package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class ImageDialogBuilder extends ViewBuilder {

    public ImageView image;

    public ImageDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);
        root.setBackgroundColor(Color.TRANSPARENT);

        image = new ImageView(mContext);
        image.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                d2p(400)
        ));
        addView(image);
    }
}

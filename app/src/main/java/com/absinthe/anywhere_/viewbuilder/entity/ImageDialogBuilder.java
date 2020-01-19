package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
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

        image = new ImageView(mContext);
        image.setLayoutParams(Params.LL.WRAP_WRAP);
        addView(image);
    }
}

package com.absinthe.anywhere_.viewbuilder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;

public class CollectorBuilder extends ViewBuilder {

    public ImageButton ibCollector;

    public CollectorBuilder(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
    }

    @Override
    public void init() {
        root.setLayoutParams(new LinearLayout.LayoutParams(
                dipToPixels(65),
                dipToPixels(65)));

        ibCollector = new ImageButton(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ibCollector.setLayoutParams(layoutParams);
        ibCollector.setImageResource(R.drawable.btn_collector);
        ibCollector.setBackground(null);
        addView(ibCollector);
    }
}

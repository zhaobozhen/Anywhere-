package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class CollectorBuilder extends ViewBuilder {

    public ImageButton ibCollector;
    public TextView tvPkgName, tvClsName;

    public CollectorBuilder(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
    }

    @Override
    public void init() {
        LinearLayout.LayoutParams wrapWrap = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapWrap.gravity = Gravity.CENTER_HORIZONTAL;

        root.setLayoutParams(wrapWrap);
        ((LinearLayout) root).setOrientation(LinearLayout.VERTICAL);

        ibCollector = new ImageButton(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dipToPixels(65),
                dipToPixels(65));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        ibCollector.setLayoutParams(layoutParams);
        ibCollector.setImageResource(R.drawable.btn_collector);
        ibCollector.setBackground(null);
        addView(ibCollector);

        if (GlobalValues.sIsCollectorPlus) {
            LinearLayout infoLayout = new LinearLayout(mContext);
            infoLayout.setLayoutParams(wrapWrap);
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            int padding = dipToPixels(5);
            infoLayout.setPadding(padding, padding, padding, padding);

            infoLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_collector_info));

            tvPkgName = new TextView(mContext);
            tvPkgName.setLayoutParams(wrapWrap);
            tvPkgName.setTextColor(Color.WHITE);
            tvPkgName.setTextSize(15);
            infoLayout.addView(tvPkgName);

            tvClsName = new TextView(mContext);
            tvClsName.setLayoutParams(wrapWrap);
            tvClsName.setTextColor(Color.WHITE);
            tvClsName.setTextSize(15);
            infoLayout.addView(tvClsName);

            addView(infoLayout);
        }
    }
}

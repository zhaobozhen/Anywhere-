package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class IconPackDialogBuilder extends ViewBuilder {

    public RecyclerView rvIconPack;

    public IconPackDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        rvIconPack = new RecyclerView(mContext);
        LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rvParams.setMargins(0, 0, 0, d2p(10));

        rvIconPack.setLayoutParams(rvParams);
        addView(rvIconPack);
    }
}

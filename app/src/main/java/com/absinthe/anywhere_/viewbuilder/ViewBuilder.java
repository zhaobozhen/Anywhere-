package com.absinthe.anywhere_.viewbuilder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.absinthe.anywhere_.utils.UiUtils;

public abstract class ViewBuilder implements IViewBuilder {
    protected ViewGroup root;
    protected Context mContext;

    ViewBuilder(Context context) {
        mContext = context;
        init();
    }

    ViewBuilder(Context context, ViewGroup viewGroup) {
        mContext = context;
        root = viewGroup;
        init();
    }

    public ViewGroup getRoot() {
        return root;
    }

    @Override
    public abstract void init();

    @Override
    public void addView(View view) {
        root.addView(view);
    }

    @Override
    public void removeView(View view) {
        root.removeView(view);
    }

    int dipToPixels(float dipValue) {
        return UiUtils.dipToPixels(mContext, dipValue);
    }
}

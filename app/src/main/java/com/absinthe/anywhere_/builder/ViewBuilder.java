package com.absinthe.anywhere_.builder;

import android.view.View;
import android.view.ViewGroup;

public abstract class ViewBuilder implements IViewBuilder {
    protected ViewGroup root;

    public ViewBuilder() {}

    public ViewGroup getRoot() {
        return root;
    }

    @Override
    public abstract void init();

    @Override
    public abstract void addView(View view);

    @Override
    public abstract void removeView(View view);
}

package com.absinthe.anywhere_.viewbuilder;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.utils.UiUtils;

/**
 * View Builder
 * <p>
 * To build a view with Java code.
 */
public abstract class ViewBuilder implements IViewBuilder {
    protected ViewGroup root;
    protected Context mContext;
    protected Resources mResources;

    protected ViewBuilder(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        init();
    }

    protected ViewBuilder(Context context, ViewGroup viewGroup) {
        mContext = context;
        mResources = mContext.getResources();
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

    protected int d2p(float dipValue) {
        return UiUtils.d2p(mContext, dipValue);
    }

    protected static class Params {

        public static class LL {

            public static LinearLayout.LayoutParams WRAP_WRAP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            public static LinearLayout.LayoutParams WRAP_MATCH = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            public static LinearLayout.LayoutParams MATCH_WRAP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            public static LinearLayout.LayoutParams MATCH_MATCH = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            public static LinearLayout.LayoutParams customParams(int width, int height) {
                return new LinearLayout.LayoutParams(width, height);
            }
        }
    }
}

package com.absinthe.anywhere_.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.utils.LogUtil;

public class WrapContentStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public WrapContentStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.e(this.getClass(), "encounter an IOOBE in RecyclerView");
        }
    }
}

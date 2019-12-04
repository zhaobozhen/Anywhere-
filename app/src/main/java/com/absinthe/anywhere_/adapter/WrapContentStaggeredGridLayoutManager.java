package com.absinthe.anywhere_.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.utils.Logger;

public class WrapContentStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public WrapContentStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Logger.e("encounter an IOOBE in RecyclerView");
        }
    }
}

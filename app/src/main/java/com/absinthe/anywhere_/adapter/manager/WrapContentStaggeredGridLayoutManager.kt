package com.absinthe.anywhere_.adapter.manager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

class WrapContentStaggeredGridLayoutManager(spanCount: Int, orientation: Int) : StaggeredGridLayoutManager(spanCount, orientation) {

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("encounter an IOOBE in RecyclerView")
        }
    }
}
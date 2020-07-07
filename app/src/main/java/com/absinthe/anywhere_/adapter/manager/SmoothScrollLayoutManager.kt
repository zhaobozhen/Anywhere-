package com.absinthe.anywhere_.adapter.manager

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class SmoothScrollLayoutManager(context: Context?) : LinearLayoutManager(context) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView,
                                        state: RecyclerView.State, position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            // 返回：滑过1px时经历的时间(ms)
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 150f / displayMetrics.densityDpi
            }
        }.apply {
            targetPosition = position
        }

        startSmoothScroll(smoothScroller)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("encounter an IOOBE in RecyclerView")
        }
    }
}
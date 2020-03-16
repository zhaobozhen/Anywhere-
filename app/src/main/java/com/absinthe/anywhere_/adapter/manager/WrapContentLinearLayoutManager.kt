package com.absinthe.anywhere_.adapter.manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import timber.log.Timber

class WrapContentLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, @RecyclerView.Orientation orientation: Int) : super(context, orientation, false)

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("encounter an IOOBE in RecyclerView")
        }
    }
}
package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.adapter.SpacesItemDecoration
import com.absinthe.anywhere_.adapter.card.AdvancedCardListAdapter
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class AdvancedCardSelectDialogBuilder(context: Context) : ViewBuilder(context) {

    private val rvList: RecyclerView
    val adapter = AdvancedCardListAdapter()

    init {
        root = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }
        rvList = RecyclerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = this@AdvancedCardSelectDialogBuilder.adapter
            addItemDecoration(SpacesItemDecoration(5))
        }
        addView(rvList)
    }
}
package com.absinthe.anywhere_.adapter.page

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class PageProvider : BaseNodeProvider() {

    override val itemViewType: Int
        get() = 2

    override val layoutId: Int
        get() = R.layout.item_page

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        val title = (data as PageNode).title
        val recyclerView = helper.getView<RecyclerView>(R.id.rv_chip)
        val adapter = ChipAdapter(title)

        when {
            adapter.itemCount == 0 -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            }
            adapter.itemCount <= 3 -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(adapter.itemCount, StaggeredGridLayoutManager.HORIZONTAL)
            }
            else -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            }
        }
        recyclerView.adapter = adapter
    }
}
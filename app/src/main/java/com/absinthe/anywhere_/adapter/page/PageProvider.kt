package com.absinthe.anywhere_.adapter.page

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.utils.AppUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class PageProvider : BaseNodeProvider() {

    override val itemViewType: Int
        get() = 2

    override val layoutId: Int
        get() = R.layout.item_page

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val title = (item as PageNode).title
        val recyclerView = helper.getView<RecyclerView>(R.id.rv_chip)
        val adapter = ChipAdapter(title)

        when {
            adapter.itemCount == 0 -> {
                recyclerView.layoutManager = WrapContentStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            }
            adapter.itemCount <= 3 -> {
                recyclerView.layoutManager = WrapContentStaggeredGridLayoutManager(adapter.itemCount, StaggeredGridLayoutManager.HORIZONTAL)
            }
            else -> {
                recyclerView.layoutManager = WrapContentStaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            AppUtils.openAnywhereEntity(context, adapter.getItem(position))
        }
        recyclerView.adapter = adapter
    }
}
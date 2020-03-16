package com.absinthe.anywhere_.adapter.page

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import java.util.*

class PageListAdapter : BaseNodeAdapter(), ItemTouchCallBack.OnItemTouchListener {

    init {
        addNodeProvider(PageTitleProvider())
        addNodeProvider(PageProvider())
        addChildClickViewIds(R.id.iv_entry)
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        return when (data[position]) {
            is PageTitleNode -> 1
            is PageNode -> 2
            else -> -1
        }
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onSwiped(position: Int) {}
}
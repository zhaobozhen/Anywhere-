package com.absinthe.anywhere_.adapter.page

import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.entity.node.BaseNode

class PageDiffUtil : DiffUtil.ItemCallback<BaseNode>() {

    override fun areItemsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
        return if (oldItem is PageNode && newItem is PageNode) {
            false
        } else {
            (oldItem as PageTitleNode).title == (newItem as PageTitleNode).title
        }
    }

    override fun areContentsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
        return false
    }
}
package com.absinthe.anywhere_.adapter.page

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

class PageTitleNode(override val childNode: MutableList<BaseNode>, val title: String) : BaseExpandNode() {

    init {
        isExpanded = false
    }

}
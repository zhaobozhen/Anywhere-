package com.absinthe.anywhere_.adapter.page

import com.chad.library.adapter.base.entity.node.BaseNode

class PageNode : BaseNode() {

    var title: String? = null

    override val childNode: MutableList<BaseNode>?
        get() = null
}
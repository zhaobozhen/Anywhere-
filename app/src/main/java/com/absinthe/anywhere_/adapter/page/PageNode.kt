package com.absinthe.anywhere_.adapter.page

import com.chad.library.adapter.base.entity.node.BaseNode
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
class PageNode : BaseNode() {

    var title: String = "Page"

    override val childNode: MutableList<BaseNode>?
        get() = null
}
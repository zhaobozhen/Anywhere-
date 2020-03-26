package com.absinthe.anywhere_.adapter.gift

import com.chad.library.adapter.base.entity.node.BaseNode

open class BaseGiftNode : BaseNode() {
    
    var msg: String? = null
    var type = 0
    
    override val childNode: MutableList<BaseNode>?
        get() = null
}
package com.absinthe.anywhere_.adapter.gift

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode

class ChatAdapter : BaseNodeAdapter() {

    init {
        addNodeProvider(LeftChatProvider())
        addNodeProvider(RightChatProvider())
        addNodeProvider(InfoProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        return when (data[position]) {
            is LeftChatNode -> {
                TYPE_LEFT
            }
            is RightChatNode -> {
                TYPE_RIGHT
            }
            is InfoNode -> {
                TYPE_INFO
            }
            else -> -1
        }
    }

    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
        const val TYPE_INFO = 2
    }
}
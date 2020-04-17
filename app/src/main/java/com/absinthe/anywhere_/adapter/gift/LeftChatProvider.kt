package com.absinthe.anywhere_.adapter.gift

import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LeftChatProvider : BaseNodeProvider() {
    
    override val itemViewType: Int
        get() = ChatAdapter.TYPE_LEFT

    override val layoutId: Int
        get() = R.layout.item_left_chat

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val msg = (item as LeftChatNode).msg
        helper.setText(R.id.tv_message, msg)
    }
}
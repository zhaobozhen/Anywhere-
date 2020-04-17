package com.absinthe.anywhere_.adapter.gift

import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class RightChatProvider : BaseNodeProvider() {

    override val itemViewType: Int
        get() = ChatAdapter.TYPE_RIGHT

    override val layoutId: Int
        get() = R.layout.item_right_chat

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val msg = (item as RightChatNode).msg
        helper.setText(R.id.tv_message, msg)
    }
}
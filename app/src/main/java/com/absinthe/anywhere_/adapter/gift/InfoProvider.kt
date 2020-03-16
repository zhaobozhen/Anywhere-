package com.absinthe.anywhere_.adapter.gift

import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class InfoProvider : BaseNodeProvider() {
    
    override val itemViewType: Int
        get() = ChatAdapter.TYPE_INFO

    override val layoutId: Int
        get() = R.layout.item_gift_info

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        val msg = (data as InfoNode).msg
        helper.setText(R.id.tv_message, msg)
    }
}
package com.absinthe.anywhere_.adapter.card

import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class AdvancedCardListAdapter : BaseQuickAdapter<AdvancedCardItem, BaseViewHolder>(R.layout.item_advanced_card) {

    override fun convert(holder: BaseViewHolder, item: AdvancedCardItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setImageResource(R.id.iv_icon, item.iconRes)
        holder.itemView.setOnClickListener(item.listener)
    }
}
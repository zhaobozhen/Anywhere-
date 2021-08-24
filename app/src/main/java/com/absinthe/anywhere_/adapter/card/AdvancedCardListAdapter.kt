package com.absinthe.anywhere_.adapter.card

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.manager.CardTypeIconGenerator
import com.absinthe.libraries.utils.extensions.dp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class AdvancedCardListAdapter :
  BaseQuickAdapter<AdvancedCardItem, BaseViewHolder>(R.layout.item_advanced_card) {

  override fun convert(holder: BaseViewHolder, item: AdvancedCardItem) {
    holder.setText(R.id.tv_title, item.title)

    holder.setImageDrawable(
      R.id.iv_icon,
      CardTypeIconGenerator.getAdvancedIcon(context, item.type, 45.dp)
    )
    holder.itemView.setOnClickListener(item.listener)
  }
}

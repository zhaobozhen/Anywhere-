package com.absinthe.anywhere_.adapter.card

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class AdvancedCardListAdapter : BaseQuickAdapter<AdvancedCardItem, BaseViewHolder>(R.layout.item_advanced_card) {

    override fun convert(holder: BaseViewHolder, item: AdvancedCardItem) {
        holder.setText(R.id.tv_title, item.title)

        holder.getView<ImageView>(R.id.iv_icon).apply {
            setImageResource(item.iconRes)
            imageTintList = ColorStateList.valueOf(Color.parseColor("#66FFFFFF"))
            backgroundTintList = ColorStateList.valueOf(context.getColor(item.backTint))
        }

        holder.itemView.setOnClickListener(item.listener)
    }
}
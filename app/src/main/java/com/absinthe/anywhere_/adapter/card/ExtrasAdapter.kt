package com.absinthe.anywhere_.adapter.card

import android.widget.Spinner
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.ExtraBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ExtrasAdapter :BaseQuickAdapter<ExtraBean.ExtraItem, BaseViewHolder>(R.layout.item_extra) {

    init {
        addChildClickViewIds(R.id.ib_delete)
    }

    override fun convert(holder: BaseViewHolder, item: ExtraBean.ExtraItem) {
        holder.getView<Spinner>(R.id.spinner).apply {
        }
    }

    override fun getItemId(position: Int): Long {
        return data[position].hashCode().toLong()
    }
}
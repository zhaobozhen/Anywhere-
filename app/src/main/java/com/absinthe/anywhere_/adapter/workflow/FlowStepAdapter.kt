package com.absinthe.anywhere_.adapter.workflow

import android.widget.TextView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class FlowStepAdapter : BaseQuickAdapter<FlowStepBean, BaseViewHolder>(R.layout.item_workflow) {

    override fun convert(holder: BaseViewHolder, item: FlowStepBean) {
        if (item.entity == null) {

        } else {
            holder.getView<TextView>(R.id.tv_app_name).apply {
                text = item.entity?.appName
                background = null
            }
            holder.getView<TextView>(R.id.tv_card_type).apply {
                text = item.entity?.type.toString()
                background = null
            }
        }
    }
}
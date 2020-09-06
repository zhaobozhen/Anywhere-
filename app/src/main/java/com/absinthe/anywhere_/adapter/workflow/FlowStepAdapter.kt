package com.absinthe.anywhere_.adapter.workflow

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class FlowStepAdapter : BaseQuickAdapter<FlowStepBean, BaseViewHolder>(R.layout.item_workflow) {

    override fun convert(holder: BaseViewHolder, item: FlowStepBean) {
        if (holder.layoutPosition == data.size - 1) {
            holder.setImageResource(R.id.iv_arrow, R.drawable.ic_workflow_arrow)
        } else {
            holder.setImageResource(R.id.iv_arrow, R.drawable.ic_workflow_line)
        }
     }
}
package com.absinthe.anywhere_.adapter.cloud

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.cloud.RuleEntity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.zhanghai.android.fastscroll.PopupTextProvider

class CloudRulesAdapter :
  BaseQuickAdapter<RuleEntity, BaseViewHolder>(R.layout.item_cloud_rules),
  PopupTextProvider {

  override fun convert(holder: BaseViewHolder, item: RuleEntity) {
    holder.setText(R.id.tv_app_name, item.name)
    holder.setText(R.id.tv_contributor, item.contributor)
  }

  override fun getPopupText(position: Int): String {
    return data[position].name.ifEmpty { " " }.first().toString()
  }

  override fun getItemId(position: Int): Long {
    return try {
      data[position].hashCode().toLong()
    } catch (e: Exception) {
      super.getItemId(position)
    }
  }
}

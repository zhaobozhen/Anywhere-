package com.absinthe.anywhere_.adapter.tile

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TileCardAdapter : BaseQuickAdapter<AppListBean?, BaseViewHolder>(R.layout.card_tile) {

  init {
    addChildClickViewIds(R.id.btn_select)
    addChildClickViewIds(R.id.iv_app_icon)
  }

  override fun convert(holder: BaseViewHolder, item: AppListBean?) {
    holder.setText(R.id.tv_title, "Tile ${holder.layoutPosition + 1}")

    item?.let {
      holder.apply {
        setText(R.id.tv_app_name, item.appName)
        setText(R.id.tv_param_1, item.packageName)
        setText(R.id.tv_param_2, item.className)
        setImageDrawable(R.id.iv_app_icon, item.icon)
      }
    }
  }
}

package com.absinthe.anywhere_.adapter.tile

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AppListBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TileCardAdapter : BaseQuickAdapter<AppListBean?, BaseViewHolder>(R.layout.card_tile) {

    override fun convert(helper: BaseViewHolder, item: AppListBean?) {
        when (helper.layoutPosition) {
            0 -> helper.setText(R.id.tv_title, "Tile One")
            1 -> helper.setText(R.id.tv_title, "Tile Two")
            2 -> helper.setText(R.id.tv_title, "Tile Three")
            else -> {
            }
        }
        if (item != null) {
            helper.setText(R.id.tv_app_name, item.appName)
            helper.setText(R.id.tv_param_1, item.packageName)
            helper.setText(R.id.tv_param_2, item.className)
            helper.setImageDrawable(R.id.iv_app_icon, item.icon)
        }
    }

    init {
        addChildClickViewIds(R.id.btn_select)
    }
}
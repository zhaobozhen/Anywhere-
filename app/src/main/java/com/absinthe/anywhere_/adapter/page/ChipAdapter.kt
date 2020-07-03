package com.absinthe.anywhere_.adapter.page

import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.UxUtils
import com.blankj.utilcode.util.Utils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.chip.Chip

class ChipAdapter internal constructor(category: String) : BaseQuickAdapter<AnywhereEntity, BaseViewHolder>(R.layout.item_chip) {

    init {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let { list ->
            for (item in list) {
                setList(list.filter { it.category == category || (it.category.isEmpty() && category == AnywhereType.Category.DEFAULT_CATEGORY) })
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: AnywhereEntity) {
        val chip: Chip = holder.getView(R.id.chip)
        chip.apply {
            text = item.appName
            chipIcon = UxUtils.getAppIcon(Utils.getApp(), item)
        }
    }
}
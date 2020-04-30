package com.absinthe.anywhere_.adapter.card

import com.absinthe.anywhere_.model.AnywhereEntity
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BaseCardAdapter : BaseDelegateMultiAdapter<AnywhereEntity, BaseViewHolder>() {

    override fun convert(holder: BaseViewHolder, item: AnywhereEntity) {

    }
}
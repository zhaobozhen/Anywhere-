package com.absinthe.anywhere_.adapter.a11y

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class A11yAdapter : BaseQuickAdapter<A11yActionBean, BaseViewHolder>(R.layout.item_a11y), DraggableModule {

    override fun convert(holder: BaseViewHolder, item: A11yActionBean) {
        TODO("Not yet implemented")
    }

}
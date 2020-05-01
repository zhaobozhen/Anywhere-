package com.absinthe.anywhere_.adapter.card

import com.absinthe.anywhere_.model.AnywhereEntity
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BaseCardAdapter() : BaseDelegateMultiAdapter<AnywhereEntity, BaseViewHolder>() {

//    constructor(var type: Int) : super() {
//        setMultiTypeDelegate(object : BaseMultiTypeDelegate<AnywhereEntity>() {
//            override fun getItemType(data: List<AnywhereEntity>, position: Int): Int {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

    override fun convert(holder: BaseViewHolder, item: AnywhereEntity) {

    }
}
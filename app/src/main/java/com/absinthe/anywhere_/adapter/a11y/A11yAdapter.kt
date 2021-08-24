package com.absinthe.anywhere_.adapter.a11y

import com.absinthe.anywhere_.adapter.a11y.bean.A11yBaseBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yCoordBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yTextBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yViewIdBean
import com.absinthe.anywhere_.adapter.a11y.provider.A11yCoordProvider
import com.absinthe.anywhere_.adapter.a11y.provider.A11yTextProvider
import com.absinthe.anywhere_.adapter.a11y.provider.A11yViewIdProvider
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.module.DraggableModule

const val TYPE_TEXT = 0
const val TYPE_VIEW_ID = 1
const val TYPE_COORD = 2

class A11yAdapter : BaseProviderMultiAdapter<A11yBaseBean>(), DraggableModule {

  init {
    addItemProvider(A11yTextProvider())
    addItemProvider(A11yViewIdProvider())
    addItemProvider(A11yCoordProvider())
  }

  override fun getItemType(data: List<A11yBaseBean>, position: Int): Int {
    return when (data[position]) {
      is A11yTextBean -> TYPE_TEXT
      is A11yViewIdBean -> TYPE_VIEW_ID
      is A11yCoordBean -> TYPE_COORD
      else -> throw IllegalArgumentException("wrong a11y provider item type")
    }
  }

}

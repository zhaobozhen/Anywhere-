package com.absinthe.anywhere_.adapter.card

import androidx.recyclerview.widget.DiffUtil
import com.absinthe.anywhere_.model.database.AnywhereEntity

class DiffListCallback : DiffUtil.ItemCallback<AnywhereEntity>() {

  override fun areItemsTheSame(oldItem: AnywhereEntity, newItem: AnywhereEntity): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: AnywhereEntity, newItem: AnywhereEntity): Boolean {
    return oldItem.appName == newItem.appName &&
      oldItem.param1 == newItem.param1 &&
      oldItem.param2 == newItem.param2 &&
      oldItem.param3 == newItem.param3 &&
      oldItem.description == newItem.description &&
      oldItem.type == newItem.type &&
      oldItem.color == newItem.color &&
      oldItem.iconUri == newItem.iconUri
  }
}

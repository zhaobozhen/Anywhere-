package com.absinthe.anywhere_.adapter.applist

import androidx.recyclerview.widget.DiffUtil
import com.absinthe.anywhere_.model.viewholder.AppListBean

class AppListDiffCallback : DiffUtil.ItemCallback<AppListBean>() {

  override fun areItemsTheSame(oldItem: AppListBean, newItem: AppListBean): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: AppListBean, newItem: AppListBean): Boolean {
    return oldItem.packageName == newItem.packageName &&
      oldItem.appName == newItem.appName &&
      oldItem.className == newItem.className &&
      oldItem.type == newItem.type
  }
}

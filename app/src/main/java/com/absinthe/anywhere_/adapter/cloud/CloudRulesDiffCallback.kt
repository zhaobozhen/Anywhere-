package com.absinthe.anywhere_.adapter.cloud

import androidx.recyclerview.widget.DiffUtil
import com.absinthe.anywhere_.model.cloud.GiteeApiContentBean

class CloudRulesDiffCallback : DiffUtil.ItemCallback<GiteeApiContentBean>() {

  override fun areItemsTheSame(
    oldItem: GiteeApiContentBean,
    newItem: GiteeApiContentBean
  ): Boolean {
    return oldItem.url == newItem.url
  }

  override fun areContentsTheSame(
    oldItem: GiteeApiContentBean,
    newItem: GiteeApiContentBean
  ): Boolean {
    return oldItem.name == newItem.name
  }
}

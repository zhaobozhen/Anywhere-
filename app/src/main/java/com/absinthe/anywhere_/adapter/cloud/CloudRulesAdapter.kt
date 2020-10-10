package com.absinthe.anywhere_.adapter.cloud

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.cloud.GitHubApiContentBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class CloudRulesAdapter : BaseQuickAdapter<GitHubApiContentBean, BaseViewHolder>(R.layout.item_cloud_rules) {

    override fun convert(holder: BaseViewHolder, item: GitHubApiContentBean) {
        holder.setText(R.id.tv_app_name, item.name)
    }

}
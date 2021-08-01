package com.absinthe.anywhere_.adapter.cloud

import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.cloud.GiteeApiContentBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.zhanghai.android.fastscroll.PopupTextProvider

class CloudRulesAdapter : BaseQuickAdapter<GiteeApiContentBean, BaseViewHolder>(R.layout.item_cloud_rules), PopupTextProvider {

    override fun convert(holder: BaseViewHolder, item: GiteeApiContentBean) {
        val trueName = item.name.removeSuffix(".json")
        val ruleName = trueName.substringBefore("@", trueName)
        val contributor = trueName.substringAfter("@", "Unknown")

        holder.setText(R.id.tv_app_name, ruleName)
        holder.setText(R.id.tv_contributor, contributor)
    }

    override fun getPopupText(position: Int): String {
        return data[position].name.ifEmpty { " " }.first().toString()
    }

    override fun getItemId(position: Int): Long {
        return try {
            data[position].url.hashCode().toLong()
        } catch (e: Exception) {
            super.getItemId(position)
        }
    }
}
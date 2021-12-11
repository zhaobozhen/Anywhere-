package com.absinthe.anywhere_.adapter.shortcut

import android.content.pm.ResolveInfo
import com.absinthe.anywhere_.R
import com.blankj.utilcode.util.AppUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ThirdAppsShortcutAdapter: BaseQuickAdapter<ResolveInfo, BaseViewHolder>(R.layout.item_app_list) {
  override fun convert(holder: BaseViewHolder, item: ResolveInfo) {
    holder.setText(R.id.tv_app_name, AppUtils.getAppName(item.activityInfo.packageName))
    holder.setImageDrawable(R.id.iv_app_icon, item.loadIcon(context.packageManager))
    holder.setText(R.id.tv_pkg_name, (item.loadLabel(context.packageManager) ?: "App").toString())
  }
}

package com.absinthe.anywhere_.adapter.applist

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.UxUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

const val MODE_APP_LIST = 0
const val MODE_APP_DETAIL = 1
const val MODE_ICON_PACK = 2
const val MODE_CARD_LIST = 3

class AppListAdapter(mode: Int) : BaseQuickAdapter<AppListBean, BaseViewHolder>(R.layout.item_app_list) {

    private var mListener: OnAppItemClickListener? = null
    private val mMode: Int = mode

    override fun convert(holder: BaseViewHolder, item: AppListBean) {
        holder.setText(R.id.tv_app_name, item.appName)

        when (mMode) {
            MODE_APP_LIST -> {
                holder.setImageDrawable(R.id.iv_app_icon, item.icon)
                holder.setText(R.id.tv_pkg_name, item.packageName)
            }
            MODE_APP_DETAIL -> {
                holder.setImageDrawable(R.id.iv_app_icon, item.icon)
                holder.setText(R.id.tv_pkg_name, item.className)

                when {
                    item.isLaunchActivity -> {
                        holder.itemView.rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.launcher_activity_background))
                    }
                    item.isExported -> {
                        holder.itemView.rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.exported_background))
                    }
                    else -> {
                        holder.itemView.rootView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }

            }
            MODE_CARD_LIST -> {
                holder.setImageDrawable(R.id.iv_app_icon, UxUtils.getAppIcon(context, item))
                holder.setText(R.id.tv_pkg_name, item.className)
            }
            MODE_ICON_PACK -> {
                holder.setImageDrawable(R.id.iv_app_icon, UxUtils.getAppIcon(context, item.packageName))
                holder.setText(R.id.tv_pkg_name, item.packageName)
            }
        }
    }

    fun setOnAppItemClickListener(listener: OnAppItemClickListener?) {
        mListener = listener
    }

    interface OnAppItemClickListener {
        fun onClick(bean: AppListBean, which: Int)
    }
}
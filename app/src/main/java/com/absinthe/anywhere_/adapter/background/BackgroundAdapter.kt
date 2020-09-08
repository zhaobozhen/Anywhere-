package com.absinthe.anywhere_.adapter.background

import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.libraries.utils.extensions.dp
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BackgroundAdapter : BaseQuickAdapter<PageEntity?, BaseViewHolder>(R.layout.item_background) {

    override fun convert(holder: BaseViewHolder, item: PageEntity?) {
        item?.let {
            val toolbar: Toolbar = holder.getView(R.id.toolbar)
            
            toolbar.apply {
                title = it.title
                inflateMenu(R.menu.main_menu)
                setNavigationIcon(R.drawable.ic_dehaze)
            }

            if (GlobalValues.isMd2Toolbar) {
                val marginHorizontal = context.resources.getDimension(R.dimen.toolbar_margin_horizontal).toInt()
                val marginVertical = context.resources.getDimension(R.dimen.toolbar_margin_vertical).toInt()
                val newLayoutParams = toolbar.layoutParams as ConstraintLayout.LayoutParams
                newLayoutParams.apply {
                    rightMargin = marginHorizontal
                    leftMargin = newLayoutParams.rightMargin
                    bottomMargin = marginVertical
                    topMargin = newLayoutParams.bottomMargin
                    height = 55.dp
                }
                toolbar.apply {
                    layoutParams = newLayoutParams
                    contentInsetStartWithNavigation = 0
                }
                UxUtils.drawMd2Toolbar(toolbar, 3)
            }

            val ivBack: ImageView = holder.getView(R.id.iv_background)
            var uri = it.backgroundUri

            if (uri.isNullOrEmpty()) {
                uri = GlobalValues.backgroundUri
            }

            Glide.with(context)
                    .load(uri)
                    .centerCrop()
                    .into(ivBack)
        }
    }
}
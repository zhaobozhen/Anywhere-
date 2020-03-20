package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class OverlayBuilder(context: Context, viewGroup: ViewGroup) : ViewBuilder(context, viewGroup) {
    lateinit var ivIcon: ImageView

    override fun init() {
        val layoutParams = LinearLayout.LayoutParams(65.dp, 65.dp)
        root.layoutParams = layoutParams

        ivIcon = ImageView(mContext).apply {
            this.layoutParams = layoutParams
            this.background = null
        }
        addView(ivIcon)
    }
}
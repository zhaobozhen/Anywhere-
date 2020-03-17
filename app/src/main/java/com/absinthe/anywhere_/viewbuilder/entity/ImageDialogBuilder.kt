package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class ImageDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var image: ImageView = ImageView(mContext)

    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_WRAP
        root.setBackgroundColor(Color.TRANSPARENT)
        root.elevation = 3.dp.toFloat()

        image.layoutParams = Params.LL.MATCH_WRAP
        image.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        addView(image)
    }
}
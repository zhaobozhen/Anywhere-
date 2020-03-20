package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class ImageDialogBuilder(context: Context) : ViewBuilder(context) {
    lateinit var image: ImageView

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP
            setBackgroundColor(Color.TRANSPARENT)
            elevation = 3.dp.toFloat()
        }

        image = ImageView(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP
            setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        addView(image)
    }
}
package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class ImageDialogBuilder(context: Context) : ViewBuilder(context) {

    var image: ImageView

    init {
        root = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            setBackgroundColor(Color.TRANSPARENT)
            elevation = 3.dp.toFloat()
        }

        image = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        addView(image)
    }
}
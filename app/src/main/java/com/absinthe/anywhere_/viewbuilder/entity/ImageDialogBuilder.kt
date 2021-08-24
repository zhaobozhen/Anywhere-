package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

class ImageDialogBuilder(context: Context) : ViewBuilder(context) {

  var image: ImageView

  init {
    root = LinearLayout(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      )
      setBackgroundColor(Color.TRANSPARENT)
      elevation = 3.dp.toFloat()
    }

    val scrollView = ScrollView(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )
      overScrollMode = ScrollView.OVER_SCROLL_NEVER
      isVerticalScrollBarEnabled = false
      setBackgroundColor(Color.TRANSPARENT)
    }
    addView(scrollView)

    image = ShapeableImageView(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
      )
      adjustViewBounds = true
      scaleType = ImageView.ScaleType.FIT_CENTER
      shapeAppearanceModel = shapeAppearanceModel.toBuilder()
        .setAllCorners(
          CornerFamily.ROUNDED,
          context.resources.getDimension(R.dimen.toolbar_radius_corner)
        )
        .build()
    }

    scrollView.addView(image)
  }
}

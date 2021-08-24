package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class OverlayBuilder(context: Context, viewGroup: ViewGroup) : ViewBuilder(context, viewGroup) {

  val ivIcon: ImageView
  val tvName: TextView

  init {
    val wrapWrap = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
      gravity = Gravity.CENTER_HORIZONTAL
    }
    (root as LinearLayout).apply {
      layoutParams = wrapWrap
      orientation = LinearLayout.VERTICAL
    }

    ivIcon = ImageView(context).apply {
      layoutParams = LinearLayout.LayoutParams(65.dp, 65.dp).apply {
        gravity = Gravity.CENTER_HORIZONTAL
      }
      this.background = null
    }
    addView(ivIcon)

    tvName = TextView(context).apply {
      layoutParams = wrapWrap.apply {
        maxWidth = 90.dp
      }
      gravity = Gravity.CENTER_HORIZONTAL

      val padding = 5.dp
      setPadding(padding, padding, padding, padding)
      background = ContextCompat.getDrawable(context, R.drawable.bg_collector_info)
      setTextColor(Color.WHITE)
      textSize = 15f
    }
    addView(tvName)
  }
}

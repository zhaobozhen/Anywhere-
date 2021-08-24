package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import com.google.android.material.slider.Slider

class IntervalDialogBuilder(context: Context) : ViewBuilder(context) {

  var slider: Slider

  init {
    root = LinearLayout(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )
      val padding = 10.dp
      setPadding(padding, padding, padding, padding)
    }

    slider = Slider(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      )
      valueFrom = 0.5f
      valueTo = 2.5f
      stepSize = 0.25f
      setLabelFormatter { value: Float -> value.toString() + "s" }
    }

    addView(slider)
  }
}

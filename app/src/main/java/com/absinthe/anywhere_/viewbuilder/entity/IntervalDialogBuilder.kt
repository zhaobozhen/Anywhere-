package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import com.google.android.material.slider.Slider

class IntervalDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var slider: Slider = Slider(mContext)

    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_MATCH

        val padding = 10.dp
        root.setPadding(padding, padding, padding, padding)

        slider.layoutParams = Params.LL.MATCH_WRAP
        slider.valueFrom = 0.5f
        slider.valueTo = 2.5f
        slider.stepSize = 0.25f
        slider.setLabelFormatter { value: Float -> value.toString() + "s" }
        addView(slider)
    }
}
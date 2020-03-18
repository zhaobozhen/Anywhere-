package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import com.google.android.material.slider.Slider

class IntervalDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var slider: Slider = Slider(mContext)

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH
            val padding = 10.dp
            setPadding(padding, padding, padding, padding)
        }

        slider.apply {
            layoutParams = Params.LL.MATCH_WRAP
            valueFrom = 0.5f
            valueTo = 2.5f
            stepSize = 0.25f
            setLabelFormatter { value: Float -> value.toString() + "s" }
        }
        addView(slider)
    }
}
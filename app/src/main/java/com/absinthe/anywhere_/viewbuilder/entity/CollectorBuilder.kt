package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CollectorBuilder(context: Context, viewGroup: ViewGroup) : ViewBuilder(context, viewGroup) {

    var ibCollector: ImageButton
    var tvPkgName = TextView(context)
    var tvClsName = TextView(context)

    init {
        val wrapWrap = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        (root as LinearLayout).apply {
            layoutParams = wrapWrap
            orientation = LinearLayout.VERTICAL
        }

        ibCollector = ImageButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(65.dp, 65.dp).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setImageResource(R.drawable.btn_collector)
            background = null
        }
        addView(ibCollector)

        if (GlobalValues.isCollectorPlus) {
            val infoLayout = LinearLayout(context).apply {
                layoutParams = wrapWrap
                orientation = LinearLayout.VERTICAL

                val padding = 5.dp
                setPadding(padding, padding, padding, padding)
                background = ContextCompat.getDrawable(context, R.drawable.bg_collector_info)
            }

            tvPkgName = TextView(context).apply {
                layoutParams = wrapWrap
                setTextColor(Color.WHITE)
                textSize = 15f
            }
            infoLayout.addView(tvPkgName)

            tvClsName = TextView(context).apply {
                layoutParams = wrapWrap
                setTextColor(Color.WHITE)
                textSize = 15f
            }
            infoLayout.addView(tvClsName)

            addView(infoLayout)
        }
    }
}
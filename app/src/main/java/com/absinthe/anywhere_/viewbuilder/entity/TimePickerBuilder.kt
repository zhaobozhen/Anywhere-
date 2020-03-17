package com.absinthe.anywhere_.viewbuilder.entity

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import com.google.android.material.button.MaterialButton

class TimePickerBuilder(context: Context) : ViewBuilder(context) {

    @JvmField
    var btnStart: MaterialButton = MaterialButton(mContext)
    @JvmField
    var btnEnd: MaterialButton = MaterialButton(mContext)

    @SuppressLint("SetTextI18n")
    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_MATCH
        (root as LinearLayout).orientation = LinearLayout.HORIZONTAL

        val padding = 10.dp
        root.setPadding(padding, padding, padding, padding)
        (root as LinearLayout).setHorizontalGravity(Gravity.CENTER)
        root.clipToPadding = false

        val wrapWrap = Params.LL.WRAP_WRAP
        btnStart.layoutParams = wrapWrap
        btnStart.setTextColor(ContextCompat.getColor(mContext, R.color.textColorNormal))
        btnStart.setBackgroundColor(ContextCompat.getColor(mContext, R.color.navigationColorNormal))
        addView(btnStart)

        val tvTo = TextView(mContext)
        tvTo.layoutParams = wrapWrap
        tvTo.setPadding(20.dp, 0, 20.dp, 0)
        tvTo.text = "To"
        addView(tvTo)

        btnEnd.layoutParams = wrapWrap
        btnEnd.setTextColor(ContextCompat.getColor(mContext, R.color.textColorNormal))
        btnEnd.setBackgroundColor(ContextCompat.getColor(mContext, R.color.navigationColorNormal))
        addView(btnEnd)
    }
}
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
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH
            orientation = LinearLayout.HORIZONTAL

            val padding = 10.dp
            setPadding(padding, padding, padding, padding)
            setHorizontalGravity(Gravity.CENTER)
            clipToPadding = false
        }

        val wrapWrap = Params.LL.WRAP_WRAP
        btnStart.apply {
            layoutParams = wrapWrap
            setTextColor(ContextCompat.getColor(mContext, R.color.textColorNormal))
            setBackgroundColor(ContextCompat.getColor(mContext, R.color.navigationColorNormal))
        }
        addView(btnStart)

        val tvTo = TextView(mContext).apply {
            layoutParams = wrapWrap
            setPadding(20.dp, 0, 20.dp, 0)
            text = "To"
        }
        addView(tvTo)

        btnEnd.apply {
            layoutParams = wrapWrap
            setTextColor(ContextCompat.getColor(mContext, R.color.textColorNormal))
            setBackgroundColor(ContextCompat.getColor(mContext, R.color.navigationColorNormal))
        }
        addView(btnEnd)
    }
}
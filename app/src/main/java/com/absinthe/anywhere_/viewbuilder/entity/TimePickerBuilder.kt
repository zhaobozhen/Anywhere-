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

    var btnStart: MaterialButton
    var btnEnd: MaterialButton

    init {
        root = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.HORIZONTAL
            clipToPadding = false

            val padding = 10.dp
            setPadding(padding, padding, padding, padding)
            setHorizontalGravity(Gravity.CENTER)
        }

        btnStart = MaterialButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
            setBackgroundColor(ContextCompat.getColor(context, R.color.navigationColorNormal))
        }
        addView(btnStart)

        @SuppressLint("SetTextI18n")
        val tvTo = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(20.dp, 0, 20.dp, 0)
            text = "To"
        }
        addView(tvTo)

        btnEnd = MaterialButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
            setBackgroundColor(ContextCompat.getColor(context, R.color.navigationColorNormal))
        }
        addView(btnEnd)
    }
}
package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.ktx.ViewKtx.dp
import com.absinthe.anywhere_.view.app.AlwaysMarqueeTextView

class StreamSingleLineItemView(context: Context) : ConstraintLayout(context) {

    val icon: AppCompatImageView = AppCompatImageView(context).apply {
        id = View.generateViewId()
        backgroundTintList = ContextCompat.getColorStateList(context, com.google.android.material.R.color.material_on_surface_emphasis_medium)
        backgroundTintMode = PorterDuff.Mode.ADD
    }

    val badge: ImageView = ImageView(context).apply {
        id = View.generateViewId()
        contentDescription = context.getString(R.string.icon_badge_todo)
        visibility = View.GONE
    }

    val indicator: ImageView = ImageView(context).apply {
        id = View.generateViewId()
    }

    val appName: AlwaysMarqueeTextView = AlwaysMarqueeTextView(context).apply {
        id = View.generateViewId()
        gravity = Gravity.CENTER_VERTICAL
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setTypeface(null, Typeface.BOLD)
        isFocusable = true
        isFocusableInTouchMode = true
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 /* Infinity */
        setTypeface(null, Typeface.BOLD)
        setHorizontallyScrolling(true)
    }

    init {
        id = R.id.card_container
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + 10.dp)

        addView(icon, LayoutParams(45.dp, 45.dp).apply {
            startToStart = this@StreamSingleLineItemView.id
            topToTop = this@StreamSingleLineItemView.id
            marginStart = 10.dp
            topMargin = 10.dp
        })

        addView(badge, LayoutParams(10.dp, 10.dp).apply {
            endToEnd = icon.id
            topToTop = icon.id
        })

        addView(indicator, LayoutParams(10.dp, 10.dp).apply {
            endToEnd = icon.id
            bottomToBottom = icon.id
        })

        addView(appName, LayoutParams(0, 45.dp).apply {
            startToEnd = icon.id
            endToEnd = this@StreamSingleLineItemView.id
            topToTop = icon.id
            bottomToBottom = icon.id
            marginStart = 10.dp
            marginEnd = 10.dp
        })
    }
}
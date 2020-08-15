package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.view.app.AlwaysMarqueeTextView
import com.squareup.contour.ContourLayout

class StreamSingleLineItemView(context: Context) : ContourLayout(context) {

    init {
        contourHeightOf { icon.bottom() + 10.ydip }
    }

    val icon: AppCompatImageView = AppCompatImageView(context).apply {
        backgroundTintList = ContextCompat.getColorStateList(context, com.google.android.material.R.color.material_on_surface_emphasis_medium)
        backgroundTintMode = PorterDuff.Mode.ADD
        applyLayout(
                x = leftTo { parent.left() + 10.xdip }.widthOf { 45.xdip },
                y = topTo { parent.top() + 10.ydip }.heightOf { 45.ydip }
        )
    }

    val badge: ImageView = ImageView(context).apply {
        contentDescription = context.getString(R.string.icon_badge_todo)
        visibility = View.GONE
        applyLayout(
                x = rightTo { icon.right() }.widthOf { 10.xdip },
                y = topTo { icon.top() }.heightOf { 10.ydip }
        )
    }

    val indicator: ImageView = ImageView(context).apply {
        applyLayout(
                x = rightTo { icon.right() }.widthOf { 10.xdip },
                y = bottomTo { icon.bottom() }.heightOf { 10.ydip }
        )
    }

    val appName: AlwaysMarqueeTextView = AlwaysMarqueeTextView(context).apply {
        setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Headline6)
        setTypeface(null, Typeface.BOLD)
        setHorizontallyScrolling(true)
        gravity = Gravity.CENTER_VERTICAL
        textSize = 17f
        isFocusable = true
        isFocusableInTouchMode = true
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 /* Infinity */
        applyLayout(
                x = leftTo { icon.right() + 10.xdip }.rightTo { parent.right() - 10.xdip },
                y = topTo { icon.top() }.bottomTo { icon.bottom() }
        )
    }
}
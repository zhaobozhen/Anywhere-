package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.view.app.AlwaysMarqueeTextView
import com.squareup.contour.ContourLayout

class StreamItemView(context: Context) : ContourLayout(context) {

    init {
        contourHeightOf { description.bottom() + 10.ydip }
    }

    val icon: AppCompatImageView = AppCompatImageView(context).apply {
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.material_on_surface_emphasis_medium)
        backgroundTintMode = PorterDuff.Mode.ADD
        applyLayout(
                x = rightTo { parent.right() - 10.xdip }.widthOf { 45.xdip },
                y = topTo { parent.top() + 10.ydip }.heightOf { 45.ydip }
        )
    }

    val badge: ImageView = ImageView(context).apply {
        contentDescription = context.getString(R.string.icon_badge_todo)
        applyLayout(
                x = rightTo { icon.right() + 7.xdip }.widthOf { 15.xdip },
                y = topTo { icon.top() - 7.ydip }.heightOf { 15.ydip }
        )
    }

    val appName: TextView = TextView(context).apply {
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
        setTypeface(null, Typeface.BOLD)
        textSize = 17f
        maxLines = 2
        applyLayout(
                x = leftTo { parent.left() + 10.xdip }.rightTo { icon.left() - 10.xdip },
                y = topTo { parent.top() + 10.ydip }.heightOf { 45.ydip }
        )
    }

    val description: AlwaysMarqueeTextView = AlwaysMarqueeTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle2)
        setTypeface(null, Typeface.BOLD)
        setHorizontallyScrolling(true)
        isFocusable = true
        isFocusableInTouchMode = true
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 /* Infinity */
        applyLayout(
                x = leftTo { parent.left() + 10.xdip }.rightTo { parent.right() - 10.xdip },
                y = topTo { icon.bottom() }
        )
    }
}
package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.ktx.ViewExtension.setTextAppearanceCompat
import com.squareup.contour.ContourLayout

class NormalItemView(context: Context) : ContourLayout(context) {

    init {
        contourHeightOf { description.bottom() + 10.ydip }
    }

    val icon: AppCompatImageView = AppCompatImageView(context).apply {
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.material_on_surface_emphasis_medium)
        backgroundTintMode = PorterDuff.Mode.ADD
        applyLayout(
                x = rightTo { parent.right() - 15.xdip }.widthOf { 45.xdip },
                y = topTo { parent.top() + 15.ydip }.heightOf { 45.ydip }
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
        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Headline6)
        setTypeface(null, Typeface.BOLD)
        applyLayout(
                x = leftTo { parent.left() + 10.xdip }.rightTo { icon.left() - 10.xdip },
                y = topTo { parent.top() + 10.ydip }
        )
    }

    val param1: TextView = TextView(context).apply {
        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Body2)
        setTypeface(null, Typeface.ITALIC)
        applyLayout(
                x = leftTo { appName.left() }.rightTo { appName.right() },
                y = topTo { appName.bottom() + context.resources.getDimension(R.dimen.cardview_line_spacing).toInt() }
        )
    }

    val param2: TextView = TextView(context).apply {
        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Body2)
        setTypeface(null, Typeface.ITALIC)
        applyLayout(
                x = leftTo { appName.left() }.rightTo { appName.right() },
                y = topTo { param1.bottom() + context.resources.getDimension(R.dimen.cardview_line_spacing).toInt() }
        )
    }

    val description: TextView = TextView(context).apply {
        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Subtitle2)
        applyLayout(
                x = leftTo { appName.left() }.rightTo { appName.right() },
                y = topTo { param2.bottom() + context.resources.getDimension(R.dimen.cardview_line_spacing).toInt() }
        )
    }
}
package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.absinthe.libraries.utils.extensions.dp
import com.absinthe.libraries.view.AlwaysMarqueeTextView

class MinimumItemView(context: Context) : ConstraintLayout(context) {

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
        visibility = View.GONE
    }

    val appName: AlwaysMarqueeTextView = AlwaysMarqueeTextView(context).apply {
        id = generateViewId()
        setTypeface(null, Typeface.BOLD)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        isSingleLine = true
        gravity = Gravity.CENTER_HORIZONTAL
    }

    init {
        id = R.id.card_container
        addPaddingBottom(8.dp)

        addView(icon, LayoutParams(32.dp, 32.dp).apply {
            endToEnd = this@MinimumItemView.id
            startToStart = this@MinimumItemView.id
            topToTop = this@MinimumItemView.id
            topMargin = 8.dp
        })

        addView(badge, LayoutParams(8.dp, 8.dp).apply {
            endToEnd = icon.id
            topToTop = icon.id
        })

        addView(indicator, LayoutParams(8.dp, 8.dp).apply {
            endToEnd = icon.id
            bottomToBottom = icon.id
        })

        addView(appName, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            topToBottom = icon.id
            topMargin = 4.dp
            marginStart = 4.dp
            marginEnd = 4.dp
        })
    }
}
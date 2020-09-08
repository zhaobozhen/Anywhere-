package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.libraries.utils.extensions.dp

class NormalItemView(context: Context) : ConstraintLayout(context) {

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

    val appName: TextView = TextView(context).apply {
        id = View.generateViewId()
        setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Headline6)
        setTypeface(null, Typeface.BOLD)
    }

    val param1: TextView = TextView(context).apply {
        id = View.generateViewId()
        setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
    }

    val param2: TextView = TextView(context).apply {
        id = View.generateViewId()
        setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
    }

    val description: TextView = TextView(context).apply {
        id = View.generateViewId()
        setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2)
    }

    init {
        id = R.id.card_container
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + 10.dp)

        addView(icon, LayoutParams(45.dp, 45.dp).apply {
            endToEnd = this@NormalItemView.id
            topToTop = this@NormalItemView.id
            marginEnd = 15.dp
            topMargin = 15.dp
        })

        addView(badge, LayoutParams(10.dp, 10.dp).apply {
            endToEnd = icon.id
            topToTop = icon.id
        })

        addView(indicator, LayoutParams(10.dp, 10.dp).apply {
            endToEnd = icon.id
            bottomToBottom = icon.id
        })

        addView(appName, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            startToStart = this@NormalItemView.id
            endToStart = icon.id
            topToTop = this@NormalItemView.id
            marginStart = 10.dp
            marginEnd = 10.dp
            topMargin = 10.dp
        })

        addView(param1, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            startToStart = appName.id
            endToEnd = appName.id
            topToBottom = appName.id
            topMargin = 4.dp
        })

        addView(param2, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            startToStart = appName.id
            endToEnd = appName.id
            topToBottom = param1.id
            topMargin = 4.dp
        })

        addView(description, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            startToStart = appName.id
            endToEnd = appName.id
            topToBottom = param2.id
            topMargin = 4.dp
        })
    }
}
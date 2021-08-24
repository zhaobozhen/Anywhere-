package com.absinthe.anywhere_.view.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.view.app.AlwaysMarqueeTextView
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.absinthe.libraries.utils.extensions.dp

class StreamItemView(context: Context) : ConstraintLayout(context) {

  val icon: AppCompatImageView = AppCompatImageView(context).apply {
    id = View.generateViewId()
    backgroundTintList = ContextCompat.getColorStateList(
      context,
      com.google.android.material.R.color.material_on_surface_emphasis_medium
    )
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
    setTypeface(null, Typeface.BOLD)
    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    maxLines = 2
  }

  val description: AlwaysMarqueeTextView = AlwaysMarqueeTextView(context).apply {
    id = View.generateViewId()
    setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2)
    setTypeface(null, Typeface.BOLD)
    setHorizontallyScrolling(true)
    isFocusable = true
    isFocusableInTouchMode = true
    isSingleLine = true
    ellipsize = TextUtils.TruncateAt.MARQUEE
    marqueeRepeatLimit = -1 /* Infinity */
  }

  init {
    id = R.id.card_container
    addPaddingBottom(10.dp)

    addView(icon, LayoutParams(45.dp, 45.dp).apply {
      endToEnd = this@StreamItemView.id
      topToTop = this@StreamItemView.id
      marginEnd = 10.dp
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
      startToStart = this@StreamItemView.id
      endToStart = icon.id
      topToTop = icon.id
      marginStart = 10.dp
      marginEnd = 10.dp
    })

    addView(description, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
      startToStart = appName.id
      endToEnd = appName.id
      topToBottom = icon.id
    })
  }
}

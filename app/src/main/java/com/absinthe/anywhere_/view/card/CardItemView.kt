package com.absinthe.anywhere_.view.card

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.absinthe.anywhere_.R
import com.google.android.material.card.MaterialCardView

@SuppressLint("ViewConstructor")
class CardItemView<T : ViewGroup>(context: Context, t: T) : MaterialCardView(context) {

    val content = t

    val appName: TextView
    val icon: AppCompatImageView
    val badge: ImageView?
    val indicator: ImageView
    val cardBackground: ImageView

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        isCheckable = true
        isClickable = true
        isFocusable = true
        transitionName = context.getString(R.string.trans_item_container)
        translationZ = context.resources.getDimension(R.dimen.cardview_elevation)
        cardElevation = context.resources.getDimension(R.dimen.cardview_elevation)
        radius = context.resources.getDimension(R.dimen.cardview_corner_radius)

        content.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        cardBackground = ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            transitionName = context.getString(R.string.trans_background)
        }

        when (content) {
            is NormalItemView -> {
                appName = content.appName
                icon = content.icon
                badge = content.badge
                indicator = content.indicator
            }
            is StreamItemView -> {
                appName = content.appName
                icon = content.icon
                badge = content.badge
                indicator = content.indicator
                addView(cardBackground)
            }
            is StreamSingleLineItemView -> {
                appName = content.appName
                icon = content.icon
                badge = content.badge
                indicator = content.indicator
                addView(cardBackground)
            }
            else -> {
                appName = TextView(context)
                icon = AppCompatImageView(context)
                badge = ImageView(context)
                indicator = ImageView(context)
            }
        }

        addView(content)
    }
}
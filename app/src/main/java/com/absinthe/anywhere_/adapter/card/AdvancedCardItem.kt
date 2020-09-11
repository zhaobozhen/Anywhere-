package com.absinthe.anywhere_.adapter.card

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

data class AdvancedCardItem(
        @StringRes val title: Int,
        @DrawableRes val iconRes: Int,
        @ColorRes val backTint: Int,
        val listener: View.OnClickListener
)
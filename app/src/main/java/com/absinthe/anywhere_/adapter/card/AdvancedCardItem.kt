package com.absinthe.anywhere_.adapter.card

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AdvancedCardItem(
        @StringRes val title: Int,
        @DrawableRes val iconRes: Int,
        val listener: View.OnClickListener
)
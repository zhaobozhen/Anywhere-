package com.absinthe.anywhere_.adapter.card

import android.view.View
import androidx.annotation.StringRes

data class AdvancedCardItem(
  @StringRes val title: Int,
  val type: Int,
  val listener: View.OnClickListener
)

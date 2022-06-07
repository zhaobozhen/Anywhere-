package com.absinthe.anywhere_.view.home

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.absinthe.libraries.utils.extensions.dpToDimensionPixelSize
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class MySpeedDialView : SpeedDialView {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  init {
    // Work around ripple bug on Android 12 when useCompatPadding = true.
    // @see https://github.com/material-components/material-components-android/issues/2617
    mainFab.apply {
      updateLayoutParams<MarginLayoutParams> {
        setMargins(context.dpToDimensionPixelSize(16))
      }
      useCompatPadding = false
    }
  }

  override fun addActionItem(
    actionItem: SpeedDialActionItem,
    position: Int,
    animate: Boolean
  ): FabWithLabelView? {
    return super.addActionItem(actionItem, position, animate)?.apply {
      fab.apply {
        updateLayoutParams<MarginLayoutParams> {
          val horizontalMargin = context.dpToDimensionPixelSize(20)
          setMargins(horizontalMargin, 0, horizontalMargin, 0)
        }
        useCompatPadding = false
      }
    }
  }
}

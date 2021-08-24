package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CreateShortcutDialogBuilder(context: Context) : ViewBuilder(context) {

  var ivIcon: ImageView
  var etName: EditText

  init {
    root = LinearLayout(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )

      val padding = 25.dp
      setPadding(padding, padding, padding, padding)
      orientation = LinearLayout.HORIZONTAL
    }

    ivIcon = ImageView(context).apply {
      layoutParams = LinearLayout.LayoutParams(45.dp, 45.dp)
    }
    addView(ivIcon)

    etName = EditText(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ).apply {
        marginStart = 10.dp
      }
      setSingleLine()
    }
    addView(etName)
  }
}

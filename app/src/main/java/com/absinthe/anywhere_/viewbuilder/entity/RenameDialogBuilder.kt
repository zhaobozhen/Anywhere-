package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class RenameDialogBuilder(context: Context) : ViewBuilder(context) {

    var etName: EditText

    init {
        root = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.HORIZONTAL

            val padding = 25.dp
            setPadding(padding, padding, padding, padding)
        }

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
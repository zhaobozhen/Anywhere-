package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CreateShortcutDialogBuilder(context: Context) : ViewBuilder(context) {
    lateinit var ivIcon: ImageView
    lateinit var etName: EditText

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH

            val padding = 25.dp
            setPadding(padding, padding, padding, padding)
            orientation = LinearLayout.HORIZONTAL
        }

        ivIcon = ImageView(mContext).apply {
            layoutParams = LinearLayout.LayoutParams(45.dp, 45.dp)
        }
        addView(ivIcon)

        etName = EditText(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP.apply {
                marginStart = 10.dp
            }
            setSingleLine()
        }
        addView(etName)
    }
}
package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CreateShortcutDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var ivIcon: ImageView = ImageView(mContext)
    @JvmField
    var etName: EditText = EditText(mContext)

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH

            val padding = 25.dp
            setPadding(padding, padding, padding, padding)
            orientation = LinearLayout.HORIZONTAL
        }

        ivIcon.layoutParams = LinearLayout.LayoutParams(45.dp, 45.dp)
        addView(ivIcon)

        etName.apply {
            layoutParams = Params.LL.MATCH_WRAP.apply {
                marginStart = 10.dp
            }
            setSingleLine()
        }
        addView(etName)
    }
}
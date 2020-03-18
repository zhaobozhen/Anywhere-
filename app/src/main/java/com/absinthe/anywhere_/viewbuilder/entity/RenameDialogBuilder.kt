package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class RenameDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var etName: EditText = EditText(mContext)

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH

            val padding = 25.dp
            setPadding(padding, padding, padding, padding)
            orientation = LinearLayout.HORIZONTAL
        }

        val etParam = Params.LL.MATCH_WRAP.apply {
            marginStart = 10.dp
        }
        etName.apply {
            layoutParams = etParam
            setSingleLine()
        }

        addView(etName)
    }
}
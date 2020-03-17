package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class RenameDialogBuilder(context: Context) : ViewBuilder(context) {
    @JvmField
    var etName: EditText = EditText(mContext)

    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_MATCH

        val padding = 25.dp
        root.setPadding(padding, padding, padding, padding)
        (root as LinearLayout).orientation = LinearLayout.HORIZONTAL

        val etParam = Params.LL.MATCH_WRAP
        etParam.marginStart = 10.dp
        etName.layoutParams = etParam
        etName.setSingleLine()

        addView(etName)
    }
}
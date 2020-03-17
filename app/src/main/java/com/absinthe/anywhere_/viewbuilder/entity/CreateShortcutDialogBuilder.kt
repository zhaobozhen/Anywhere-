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
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_MATCH

        val padding = 25.dp
        root.setPadding(padding, padding, padding, padding)
        (root as LinearLayout).orientation = LinearLayout.HORIZONTAL

        ivIcon.layoutParams = LinearLayout.LayoutParams(45.dp, 45.dp)
        addView(ivIcon)

        val etParam = Params.LL.MATCH_WRAP
        etParam.marginStart = 10.dp
        etName.layoutParams = etParam
        etName.setSingleLine()
        addView(etName)
    }
}
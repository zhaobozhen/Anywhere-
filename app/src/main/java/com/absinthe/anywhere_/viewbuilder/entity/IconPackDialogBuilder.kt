package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class IconPackDialogBuilder(context: Context?) : ViewBuilder(context!!) {
    @JvmField
    var rvIconPack: RecyclerView = RecyclerView(mContext)

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_MATCH
        }

        rvIconPack = RecyclerView(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP.apply {
                setMargins(0, 0, 0, 10.dp)
            }
        }

        addView(rvIconPack)
    }
}
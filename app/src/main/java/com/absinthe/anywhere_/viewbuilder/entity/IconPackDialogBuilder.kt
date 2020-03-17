package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class IconPackDialogBuilder(context: Context?) : ViewBuilder(context!!) {
    @JvmField
    var rvIconPack: RecyclerView = RecyclerView(mContext)

    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_MATCH

        val rvParams = Params.LL.MATCH_WRAP
        rvParams.setMargins(0, 0, 0, 10.dp)
        rvIconPack.layoutParams = rvParams

        addView(rvIconPack)
    }
}
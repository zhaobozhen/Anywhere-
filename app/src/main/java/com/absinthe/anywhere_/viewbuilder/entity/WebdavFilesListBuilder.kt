package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class WebdavFilesListBuilder(context: Context) : ViewBuilder(context) {

    var rvIconPack: RecyclerView

    init {
        root = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
        }

        rvIconPack = RecyclerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 10.dp)
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
        }

        addView(rvIconPack)
    }
}
package com.absinthe.anywhere_.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacesItemDecoration(private val space: Int) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (parent.paddingLeft != space) {
            parent.setPadding(space, space, space, space)
            parent.clipToPadding = false
        }

        outRect.apply {
            left = space
            right = space
            bottom = space / 2
            top = space / 2
        }
    }

}
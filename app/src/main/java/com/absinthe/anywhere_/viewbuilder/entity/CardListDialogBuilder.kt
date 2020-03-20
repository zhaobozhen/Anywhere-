package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CardListDialogBuilder(context: Context) : ViewBuilder(context) {
    lateinit var mAdapter: AppListAdapter

    override fun init() {
        mAdapter = AppListAdapter(mContext, AppListAdapter.MODE_CARD_LIST)
        root = ViewFlipper(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 10.dp, 0, 10.dp)
        }

        val rvCardList = RecyclerView(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter
        }
        addView(rvCardList)

        val tvEmpty = TextView(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(10.dp, 10.dp, 10.dp, 10.dp)
            gravity = Gravity.CENTER_HORIZONTAL
            textSize = 24f
            setTextColor(ContextCompat.getColor(mContext, R.color.textColorNormal))
            setText(R.string.dialog_empty_list)
        }
        addView(tvEmpty)
    }

    fun setOnItemClickListener(listener: AppListAdapter.OnItemClickListener?) {
        mAdapter.setOnItemClickListener(listener)
    }
}
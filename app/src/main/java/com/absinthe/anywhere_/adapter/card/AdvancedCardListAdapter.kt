package com.absinthe.anywhere_.adapter.card

import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class AdvancedCardListAdapter : BaseQuickAdapter<AdvancedCardItem, BaseViewHolder>(0) {

    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textSize = 15f
            gravity = Gravity.CENTER
            compoundDrawablePadding = 5.dp
            isClickable = true

            setPadding(15.dp, 15.dp, 15.dp, 15.dp)
            setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
            setTypeface(null, Typeface.BOLD)

            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }

        return createBaseViewHolder(itemView)
    }

    override fun convert(holder: BaseViewHolder, item: AdvancedCardItem) {
        (holder.itemView as TextView).apply {
            setText(item.title)
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, item.iconRes, 0, 0)
        }
    }
}
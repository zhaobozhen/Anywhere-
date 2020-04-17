package com.absinthe.anywhere_.adapter.defrost

import android.view.View
import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton

class DefrostAdapter : BaseQuickAdapter<DefrostItem, BaseViewHolder>(R.layout.item_defrost_mode) {

    private var position = -1

    init {
        addChildClickViewIds(R.id.button)
    }

    override fun convert(holder: BaseViewHolder, item: DefrostItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_summary, item.summary)

        val button: MaterialButton = holder.getView(R.id.button)
        val radio: MaterialRadioButton = holder.getView(R.id.radio)

        if (item.buttonText.isEmpty()) {
            button.visibility = View.GONE
        }
        button.text = item.buttonText

        holder.itemView.setOnClickListener {
            if (position != -1) {
                val oldRadio: MaterialRadioButton = getViewByPosition(position, R.id.radio) as MaterialRadioButton
                oldRadio.isChecked = false
            }
            position = holder.adapterPosition
            radio.isChecked = true
        }
    }

}
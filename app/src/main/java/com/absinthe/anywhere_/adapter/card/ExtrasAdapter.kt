package com.absinthe.anywhere_.adapter.card

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ExtrasAdapter :BaseQuickAdapter<ExtraBean.ExtraItem, BaseViewHolder>(R.layout.item_extra) {

    init {
        addChildClickViewIds(R.id.ib_delete)
    }

    override fun convert(holder: BaseViewHolder, item: ExtraBean.ExtraItem) {
        holder.getView<Spinner>(R.id.spinner).apply {
            setSelection(TYPE_LIST.indexOf(item.type))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    item.type = TYPE_LIST[position]
                }
            }
        }

        holder.getView<EditText>(R.id.et_key).apply {
            if (item.key.isNotEmpty()) {
                setText(item.key)
            }
            addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(text: Editable) {
                    item.key = text.toString()
                }
                override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            })
        }

        holder.getView<EditText>(R.id.et_value).apply {
            if (item.value.isNotEmpty()) {
                setText(item.value)
            }
            addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(text: Editable) {
                    item.value = text.toString()
                }
                override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            })
        }
    }

    companion object {
        val TYPE_LIST = listOf(
                TYPE_STRING, TYPE_INT, TYPE_LONG, TYPE_BOOLEAN, TYPE_FLOAT, TYPE_URI
        )
    }
}
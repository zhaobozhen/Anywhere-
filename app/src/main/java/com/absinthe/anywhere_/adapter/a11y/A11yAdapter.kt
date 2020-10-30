package com.absinthe.anywhere_.adapter.a11y

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class A11yAdapter : BaseQuickAdapter<A11yActionBean, BaseViewHolder>(0), DraggableModule {

    init {
        addChildClickViewIds(R.id.ib_remove)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return createBaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_a11y_text, parent, false))
    }

    override fun convert(holder: BaseViewHolder, item: A11yActionBean) {
        holder.getView<TextInputEditText>(R.id.tiet_text).apply {
            setText(item.content)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.content = s.toString()
                }
            })
        }
        holder.getView<TextInputEditText>(R.id.tiet_activity_id).apply {
            setText(item.activityId)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.activityId = s.toString()
                }
            })
        }

        holder.getView<SwitchMaterial>(R.id.switch_contains).apply {
            isChecked = item.contains
            setOnCheckedChangeListener { _, isChecked ->
                item.contains = isChecked
            }
        }
        holder.getView<EditText>(R.id.et_delay_time).apply {
            if (item.delay != 0L) {
                setText(item.delay.toString())
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    try {
                        item.delay = s.toString().toLong()
                    } catch (e: Exception) {

                    }
                }
            })
        }
    }

}
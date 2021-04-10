package com.absinthe.anywhere_.adapter.a11y.provider

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yType
import com.absinthe.anywhere_.adapter.a11y.TYPE_VIEW_ID
import com.absinthe.anywhere_.adapter.a11y.bean.A11yBaseBean
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.textfield.TextInputEditText
import java.lang.IllegalArgumentException

class A11yViewIdProvider : BaseItemProvider<A11yBaseBean>() {

    override val itemViewType: Int = TYPE_VIEW_ID
    override val layoutId: Int = R.layout.item_a11y_view_id

    init {
        addChildClickViewIds(R.id.ib_remove)
    }

    override fun convert(helper: BaseViewHolder, item: A11yBaseBean) {
        helper.getView<TextView>(R.id.tv_title).apply {
            text = when (item.actionBean.type) {
                A11yType.VIEW_ID -> context.getString(R.string.bsd_a11y_menu_click_view_id)
                A11yType.LONG_PRESS_VIEW_ID -> context.getString(R.string.bsd_a11y_menu_long_press_view_id)
                else -> throw IllegalArgumentException("wrong a11y type")
            }
        }
        helper.getView<TextInputEditText>(R.id.tiet_text).apply {
            setText(item.actionBean.content)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.actionBean.content = s.toString()
                }
            })
        }
        helper.getView<TextInputEditText>(R.id.tiet_activity_id).apply {
            setText(item.actionBean.activityId)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.actionBean.activityId = s.toString()
                }
            })
        }
        helper.getView<EditText>(R.id.et_delay_time).apply {
            if (item.actionBean.delay != 0L) {
                setText(item.actionBean.delay.toString())
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    try {
                        item.actionBean.delay = s.toString().toLong()
                    } catch (ignore: Exception) {
                    }
                }
            })
        }
    }

    override fun onChildClick(
        helper: BaseViewHolder,
        view: View,
        data: A11yBaseBean,
        position: Int
    ) {
        if (view.id == R.id.ib_remove) {
            getAdapter()?.remove(data)
        }
    }
}
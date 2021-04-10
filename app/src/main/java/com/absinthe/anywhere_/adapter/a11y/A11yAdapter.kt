package com.absinthe.anywhere_.adapter.a11y

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isGone
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.absinthe.anywhere_.a11y.A11yType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class A11yAdapter : BaseQuickAdapter<A11yActionBean, BaseViewHolder>(0), DraggableModule {

    init {
        addChildClickViewIds(R.id.ib_remove)
    }

    private val nodeEditMenu by lazy {
        mapOf(
            A11yType.TEXT to context.getString(R.string.bsd_a11y_menu_click_text),
            A11yType.LONG_PRESS_TEXT to context.getString(R.string.bsd_a11y_menu_long_press_text),
            A11yType.VIEW_ID to context.getString(R.string.bsd_a11y_menu_click_view_id),
            A11yType.LONG_PRESS_VIEW_ID to context.getString(R.string.bsd_a11y_menu_long_press_view_id)
        )
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return createBaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_a11y_text, parent, false))
    }

    override fun convert(holder: BaseViewHolder, item: A11yActionBean) {
        holder.getView<TextView>(R.id.tv_title).apply {
            text = nodeEditMenu[item.type]
        }
        holder.getView<TextInputLayout>(R.id.til_text).apply {
            if (item.type == A11yType.VIEW_ID || item.type == A11yType.LONG_PRESS_VIEW_ID) {
                hint = "View ID"
                helperText = context.getString(R.string.bsd_text_input_layout_content_helper_text_view_id)
            } else if (item.type == A11yType.TEXT || item.type == A11yType.LONG_PRESS_TEXT) {
                hint = context.getString(R.string.bsd_text_input_layout_content_hint)
                helperText = context.getString(R.string.bsd_text_input_layout_content_helper_text)
            } else if (item.type == A11yType.COORDINATE || item.type == A11yType.LONG_PRESS_COORDINATE) {
                hint = context.getString(R.string.bsd_text_input_layout_content_hint)
                helperText = context.getString(R.string.bsd_text_input_layout_content_helper_text)
            }
        }
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
            isGone = item.type != A11yType.TEXT && item.type != A11yType.LONG_PRESS_TEXT
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
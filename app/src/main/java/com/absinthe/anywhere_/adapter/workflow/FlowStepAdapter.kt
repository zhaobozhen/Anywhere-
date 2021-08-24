package com.absinthe.anywhere_.adapter.workflow

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class FlowStepAdapter : BaseQuickAdapter<FlowStepBean, BaseViewHolder>(R.layout.item_workflow),
  DraggableModule {

  override fun convert(holder: BaseViewHolder, item: FlowStepBean) {
    if (item.entity == null) {
      holder.getView<TextView>(R.id.tv_app_name).apply {
        text = "               "
        setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
      }
      holder.getView<TextView>(R.id.tv_card_type).apply {
        text = "                            "
        setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
      }
      holder.getView<EditText>(R.id.et_delay_time).apply {
        setText("")
      }
    } else {
      holder.getView<TextView>(R.id.tv_app_name).apply {
        text = item.entity?.appName
        background = null
      }
      holder.getView<TextView>(R.id.tv_card_type).apply {
        text = context.getString(
          AnywhereType.Card.TYPE_STRINGRES_MAP[item.entity?.type]
            ?: android.R.string.unknownName
        )
        background = null
      }
      holder.getView<EditText>(R.id.et_delay_time).apply {
        setText(item.delay.toString())
      }
      holder.getView<EditText>(R.id.et_delay_time).addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
          if (s.isNullOrEmpty()) {
            item.delay = 0
          } else {
            item.delay = s.toString().toLong()
          }
        }
      })
    }
  }
}

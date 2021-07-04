package com.absinthe.anywhere_.adapter.defrost

import android.app.admin.DevicePolicyManager
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.catchingnow.delegatedscopeclient.DSMClient
import com.catchingnow.icebox.sdk_client.IceBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.radiobutton.MaterialRadioButton

class DefrostAdapter : BaseQuickAdapter<DefrostItem, BaseViewHolder>(R.layout.item_defrost_mode) {

    private var mPosition = -1

    init {
        addChildClickViewIds(R.id.button)
    }

    override fun convert(holder: BaseViewHolder, item: DefrostItem) {
        val button: TextView = holder.getView(R.id.button)
        val radio: MaterialRadioButton = holder.getView(R.id.radio)
        val addition: TextView = holder.getView(R.id.tv_addition)

        if (item.mode == GlobalValues.defrostMode) {
            radio.isChecked = true
            mPosition = holder.bindingAdapterPosition
        }

        button.apply {
            if (item.buttonText.isEmpty()) {
                visibility = View.GONE
            } else {
                text = item.buttonText
            }

            when (item.mode) {
                Const.DEFROST_MODE_DSM -> {
                    if (DSMClient.getDelegatedScopes(context).contains(DevicePolicyManager.DELEGATION_PACKAGE_ACCESS)) {
                        isEnabled = false
                        text = context.getText(R.string.btn_acquired)
                    }
                }
                Const.DEFROST_MODE_ICEBOX_SDK -> {
                    if (ContextCompat.checkSelfPermission(context, IceBox.SDK_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                        isEnabled = false
                        text = context.getText(R.string.btn_acquired)
                    }
                }
            }
        }

        addition.apply {
            if (item.addition.isEmpty()) {
                visibility = View.GONE
            } else {
                text = item.addition
            }
        }

        holder.apply {
            setText(R.id.tv_title, item.title)
            setText(R.id.tv_summary, item.summary)

            itemView.setOnClickListener {
                if (mPosition != -1) {
                    val oldRadio: MaterialRadioButton = getViewByPosition(mPosition, R.id.radio) as MaterialRadioButton
                    oldRadio.isChecked = false
                }
                mPosition = holder.bindingAdapterPosition
                radio.isChecked = true
                GlobalValues.defrostMode = item.mode
            }
        }

    }

}
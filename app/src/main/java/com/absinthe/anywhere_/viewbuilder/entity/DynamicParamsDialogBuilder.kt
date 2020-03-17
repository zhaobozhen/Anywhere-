package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import timber.log.Timber

class DynamicParamsDialogBuilder(context: Context) : ViewBuilder(context) {

    private val editTextList: MutableList<EditText> = ArrayList()
    private lateinit var params: Array<String>

    override fun init() {
        root = LinearLayout(mContext)
        root.layoutParams = Params.LL.MATCH_WRAP

        val padding = mResources.getDimension(R.dimen.bsd_edit_text_margin_horizontal).toInt()
        root.setPadding(padding, 0, padding, 0)
        (root as LinearLayout).orientation = LinearLayout.VERTICAL
    }

    fun setParams(paramString: String) {
        Timber.d(paramString)
        params = paramString.split("&").toTypedArray()

        for (para in params) {
            Timber.d(para)
            val editText = EditText(mContext).apply {
                layoutParams = Params.LL.MATCH_WRAP
                isSingleLine = true
                hint = para
            }

            addView(editText)
            editTextList.add(editText)
        }
    }

    val inputParams: String
        get() {
            val sb = StringBuilder()
            sb.append("?")
            var iter = 0
            val len = params.size
            while (iter < len) {
                sb.append(params[iter])
                        .append("=")
                        .append(if (editTextList[iter].text == null) "" else editTextList[iter].text.toString())
                if (iter != len - 1) {
                    sb.append("&")
                }
                iter++
            }
            Timber.d(sb.toString())
            return sb.toString()
        }
}
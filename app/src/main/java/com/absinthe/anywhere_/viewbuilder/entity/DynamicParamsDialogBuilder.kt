package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import timber.log.Timber

class DynamicParamsDialogBuilder(context: Context) : ViewBuilder(context) {

    private val paramsMap: MutableMap<String, EditText> = mapOf<String, EditText>().toMutableMap()

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP

            val padding = mResources.getDimension(R.dimen.bsd_edit_text_margin_horizontal).toInt()
            setPadding(padding, 0, padding, 0)
            orientation = LinearLayout.VERTICAL
        }
    }

    fun setParams(paramString: String) {
        Timber.d(paramString)
        val params = paramString.split("&").toTypedArray()

        for (para in params) {
            val editText = EditText(mContext).apply {
                layoutParams = Params.LL.MATCH_WRAP
                isSingleLine = true
                hint = para
            }

            addView(editText)
            paramsMap[para] = editText
        }
    }

    val inputParams: String
        get() {
            val sb = StringBuilder().apply {
                append("?")

                for (iter in paramsMap) {
                    append("${iter.key}=${iter.value.text}&")
                }

                removeSuffix("&")
            }

            Timber.d(sb.toString())
            return sb.toString()
        }
}
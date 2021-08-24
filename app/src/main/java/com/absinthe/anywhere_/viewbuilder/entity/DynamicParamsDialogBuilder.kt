package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.doOnMainThreadIdle
import com.absinthe.anywhere_.viewbuilder.ViewBuilder
import timber.log.Timber

class DynamicParamsDialogBuilder(context: Context) : ViewBuilder(context) {

  private val paramsMap: MutableMap<String, EditText> = mutableMapOf()

  init {
    root = LinearLayout(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      )

      val padding = context.resources.getDimension(R.dimen.bsd_edit_text_margin_horizontal).toInt()
      setPadding(padding, 0, padding, 0)
      orientation = LinearLayout.VERTICAL
    }
  }

  fun setParams(paramString: String) {
    Timber.d(paramString)
    val params = paramString.split("&").toTypedArray()

    for (para in params) {
      val editText = EditText(mContext).apply {
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
        )
        isSingleLine = true
        hint = para
      }

      addView(editText)
      paramsMap[para] = editText
    }
    paramsMap[params[0]]?.apply {
      requestFocus()

      doOnMainThreadIdle({
        val inputManager =
          context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
      })
    }

  }

  val inputParams: String
    get() {
      val sb = StringBuilder().apply {
        append("?")

        for (iterator in paramsMap) {
          append("${iterator.key}=${iterator.value.text}&")
        }

        removeSuffix("&")
      }

      Timber.d(sb.toString())
      return sb.toString()
    }
}

package com.absinthe.anywhere_.view.card

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.ClipboardUtil
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.view.app.BottomSheetHeaderView
import com.absinthe.anywhere_.view.app.IHeaderView
import com.absinthe.libraries.utils.extensions.dp
import com.absinthe.libraries.utils.extensions.getResourceIdByAttr
import com.absinthe.libraries.utils.extensions.paddingBottomCompat

class ShellResultContentView(context: Context) : LinearLayout(context), IHeaderView {

  private val header = BottomSheetHeaderView(context).apply {
    layoutParams =
      LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    title.text = context.getString(R.string.dialog_shell_result_title)
  }

  private val container = ScrollView(context).apply {
    layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    paddingBottomCompat = 24.dp
    clipToPadding = false
  }

  private val copyIcon = AppCompatImageButton(context).apply {
    layoutParams = LayoutParams(48.dp, 48.dp).also {
      it.gravity = Gravity.CENTER_HORIZONTAL
    }
    setImageResource(R.drawable.ic_copy)
    setBackgroundResource(context.getResourceIdByAttr(android.R.attr.selectableItemBackgroundBorderless))
    setOnClickListener {
      ClipboardUtil.put(context, content.text.toString())
      ToastUtil.makeText(context, R.string.toast_copied)
    }
  }

  val content = AppCompatTextView(context).apply {
    layoutParams = LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    ).also {
      it.topMargin = 24.dp
    }
  }

  init {
    orientation = VERTICAL
    setPadding(24.dp, 16.dp, 24.dp, 0)
    addView(header)
    addView(copyIcon)
    addView(container)
    container.addView(content)
  }

  override fun getHeaderView(): BottomSheetHeaderView {
    return header
  }
}

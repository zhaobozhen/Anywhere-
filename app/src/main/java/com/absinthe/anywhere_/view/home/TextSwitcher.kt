package com.absinthe.anywhere_.view.home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.absinthe.anywhere_.R

/**
 * <pre>
 * author : Absinthe
 * time : 2020/09/14
 * </pre>
 */
class TextSwitcherView : TextSwitcher, ViewSwitcher.ViewFactory {

    var textColor: Int = Color.WHITE

    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setFactory(this)
        this.setInAnimation(context, R.anim.anim_text_switcher_in)
        this.setOutAnimation(context, R.anim.anim_text_switcher_out)
        textColor = context.getColor(R.color.textColorNormal)
    }

    override fun makeView(): View {
        return TextView(context).apply {
            layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.START or Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        }
    }

    override fun setText(text: CharSequence) {
        val span = ForegroundColorSpan(textColor)
        super.setText(SpannableString(text).apply {
            setSpan(span, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
    }
}
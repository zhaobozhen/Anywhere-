package com.absinthe.anywhere_.view.home

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.utils.UxUtils

/**
 * <pre>
 * author : Absinthe
 * time : 2020/09/14
 * </pre>
 */
class TextSwitcherView : TextSwitcher, ViewSwitcher.ViewFactory {

    private var text: TextView? = null

    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setFactory(this)
        this.setInAnimation(context, R.anim.anim_text_switcher_in)
        this.setOutAnimation(context, R.anim.anim_text_switcher_out)
    }

    override fun makeView(): View {
        text = TextView(context).apply {
            layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.START or Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(context.getColor(R.color.textColorNormal))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        }
        return text!!
    }

    override fun setText(text: CharSequence?) {
        super.setText(text)
        (nextView as TextView).setTextColor(UxUtils.getTopWidgetColor(GlobalValues.actionBarType))
    }
}
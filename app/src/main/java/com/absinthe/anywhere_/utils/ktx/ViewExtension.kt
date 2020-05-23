package com.absinthe.anywhere_.utils.ktx

import android.widget.TextView
import androidx.annotation.StyleRes
import com.absinthe.anywhere_.utils.AppUtils

object ViewExtension {

    fun TextView.setTextAppearanceCompat(@StyleRes resId: Int) {
        if (AppUtils.atLeastM()) {
            setTextAppearance(resId)
        } else {
            setTextAppearance(context, resId)
        }
    }
}
package com.absinthe.anywhere_.utils.ktx

import android.content.res.Resources

object ViewKtx {
    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

    val Number.sp: Float get() = (toInt() * Resources.getSystem().displayMetrics.scaledDensity)
}
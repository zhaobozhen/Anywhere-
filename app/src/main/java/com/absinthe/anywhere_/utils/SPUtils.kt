package com.absinthe.anywhere_.utils

import android.content.Context
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.constants.Const

object SPUtils {
    var sPName: String? = null
        get() {
            if (field == null) {
                field = if (BuildConfig.DEBUG) {
                    Const.SP_NAME_DEBUG
                } else {
                    Const.SP_NAME
                }
            }
            return field
        }

    @JvmStatic
    fun putString(context: Context, key: String?, value: String?) {
        context.getSharedPreferences(sPName, Context.MODE_PRIVATE).edit().apply {
            putString(key, value)
            apply()
        }
    }

    fun getString(context: Context, key: String?, defaultValue: String? = ""): String {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getString(key, defaultValue) ?: ""
    }
}
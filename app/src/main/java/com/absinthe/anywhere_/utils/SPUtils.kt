package com.absinthe.anywhere_.utils

import android.content.Context
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.Settings

object SPUtils {
    private var sPName: String? = null
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

    fun getString(context: Context, key: String?): String {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getString(key, "") ?: ""
    }

    @JvmStatic
    fun getString(context: Context, key: String?, defaultValue: String?): String {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getString(key, defaultValue) ?: ""
    }

    @JvmStatic
    fun putBoolean(context: Context, key: String?, value: Boolean) {
        context.getSharedPreferences(sPName, Context.MODE_PRIVATE).edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String?, defaultValue: Boolean): Boolean {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getBoolean(key, defaultValue)
    }

    @JvmStatic
    fun putInt(context: Context, key: String?, value: Int) {
        context.getSharedPreferences(sPName, Context.MODE_PRIVATE).edit().apply {
            putInt(key, value)
            apply()
        }
    }

    fun getInt(context: Context, key: String?): Int {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getInt(key, 0)
    }

    @JvmStatic
    fun getInt(context: Context, key: String?, defaultValue: Int): Int {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getInt(key, defaultValue)
    }

    @JvmStatic
    fun putLong(context: Context, key: String?, value: Long) {
        context.getSharedPreferences(sPName, Context.MODE_PRIVATE).edit().apply {
            putLong(key, value)
            apply()
        }
    }

    fun getLong(context: Context, key: String?): Long {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getLong(key, 0)
    }

    fun getLong(context: Context, key: String?, defaultValue: Long): Long {
        val sp = context.getSharedPreferences(sPName, Context.MODE_PRIVATE)
        return sp.getLong(key, defaultValue)
    }

    fun putToken(context: Context, value: String?) {
        context.getSharedPreferences(Const.TOKEN_SP_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(Const.PREF_TOKEN, value)
            apply()
        }
        Settings.sToken = value
    }

    fun getToken(context: Context): String? {
        val sp = context.getSharedPreferences(Const.TOKEN_SP_NAME, Context.MODE_PRIVATE)
        return sp.getString(Const.PREF_TOKEN, "")
    }
}
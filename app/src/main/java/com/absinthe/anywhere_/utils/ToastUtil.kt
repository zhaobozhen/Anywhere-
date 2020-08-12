package com.absinthe.anywhere_.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.manager.ActivityStackManager

object ToastUtil {
    /**
     * make a toast via a string
     *
     * @param text a string text
     */
    fun makeText(text: String) {
        Toasty.show(ActivityStackManager.topActivity!!, text)
    }

    /**
     * make a toast via a resource id
     *
     * @param resId a string resource id
     */
    @JvmStatic
    fun makeText(@StringRes resId: Int) {
        Toasty.show(ActivityStackManager.topActivity!!, resId)
    }

    /**
     * <pre>
     * author : Absinthe
     * time : 2020/08/12
     * </pre>
     */
    object Toasty {

        fun show(context: Context, message: String) {
            show(context, message, Toast.LENGTH_SHORT)
        }

        fun show(context: Context, @StringRes res: Int) {
            show(context, context.getString(res), Toast.LENGTH_SHORT)
        }

        fun showLong(context: Context, message: String) {
            show(context, message, Toast.LENGTH_LONG)
        }

        fun showLong(context: Context, @StringRes res: Int) {
            show(context, context.getString(res), Toast.LENGTH_LONG)
        }

        private fun show(context: Context, message: String, duration: Int) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null)

            view.findViewById<TextView>(R.id.message).apply {
                text = message
            }

            Toast(context).apply {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 200)
                this.duration = duration
                this.view = view
            }.show()
        }
    }
}
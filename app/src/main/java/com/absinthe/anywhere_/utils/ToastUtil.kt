package com.absinthe.anywhere_.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.StringRes
import com.absinthe.anywhere_.databinding.LayoutToastBinding
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import org.jetbrains.annotations.NotNull

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
     * make a toast via a string
     *
     * @param context context
     * @param text a string text
     */
    fun makeText(context: Context, text: String) {
        if (context is Activity) {
            Toasty.show(context, text)
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * make a toast via a resource id
     *
     * @param context context
     * @param resId a string resource id
     */
    @JvmStatic
    fun makeText(context: Context, @StringRes resId: Int) {
        if (context is Activity) {
            Toasty.show(context, resId)
        } else {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        }
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
            val view = LayoutToastBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as @NotNull LayoutInflater)

            view.message.text = message

            Toast(context).apply {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 200)
                this.duration = duration
                this.view = view.root
            }.show()
        }
    }
}
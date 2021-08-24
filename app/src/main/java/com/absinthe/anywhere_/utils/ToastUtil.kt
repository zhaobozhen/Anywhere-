package com.absinthe.anywhere_.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import com.absinthe.anywhere_.AwContextWrapper
import com.absinthe.anywhere_.view.app.ToastView
import com.blankj.utilcode.util.Utils
import java.lang.ref.WeakReference

object ToastUtil {

  private val contextWrapper by lazy { AwContextWrapper(Utils.getApp()) }
  private val handler = Handler(Looper.getMainLooper())
  private var toast: WeakReference<Toast?>? = null

  /**
   * make a toast via a string
   *
   * @param context context
   * @param text a string text
   */
  fun makeText(context: Context, text: String) {
    if (Looper.getMainLooper().thread === Thread.currentThread()) {
      Toasty.show(context, text)
    } else {
      handler.post { Toasty.show(context, text) }
    }
  }

  /**
   * make a toast via a resource id
   *
   * @param context context
   * @param resId a string resource id
   */
  fun makeText(context: Context, @StringRes resId: Int) {
    makeText(context, context.getString(resId))
  }

  @JvmStatic
  fun makeText(text: String) {
    makeText(contextWrapper, text)
  }

  @JvmStatic
  fun makeText(@StringRes resId: Int) {
    makeText(contextWrapper, resId)
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
      toast?.get()?.cancel()
      toast = null

      if (AppUtils.atLeastR() && context !is ContextThemeWrapper) {
        Toast(context).also {
          it.duration = duration
          it.setText(message)
          toast = WeakReference(it)
        }.show()
      } else {
        val ctx = if (context is ContextThemeWrapper) {
          context
        } else {
          contextWrapper
        }
        val view = ToastView(ctx).also {
          it.message.text = message
        }
        Toast(ctx).also {
          it.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 200)
          it.duration = duration
          it.view = view
          toast = WeakReference(it)
        }.show()
      }
    }
  }
}

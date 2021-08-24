package com.absinthe.anywhere_.utils.handler

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.listener.OnAppDefrostListener
import com.absinthe.anywhere_.utils.ToastUtil
import com.catchingnow.icebox.sdk_client.IceBox
import java.net.URISyntaxException

object URLSchemeHandler {

  @Throws(Exception::class)
  fun parse(context: Context, url: String, packageName: String? = null, action: () -> Unit = {}) {
    if (!packageName.isNullOrEmpty() && IceBox.getAppEnabledSetting(context, packageName) != 0) {
      val result = DefrostHandler.defrost(context, packageName, object : OnAppDefrostListener {
        override fun onAppDefrost() {
          try {
            context.startActivity(handleIntent(url))
            action()
          } catch (e: Throwable) {
            throw e
          }
        }
      })
      if (!result) {
        ToastUtil.makeText(context, R.string.toast_not_choose_defrost_mode)
      }
    } else {
      try {
        context.startActivity(handleIntent(url))
        action()
      } catch (e: Throwable) {
        action()
        throw e
      }
    }
  }

  @Throws(Exception::class)
  fun parse(activity: Activity, url: String, packageName: String? = null, action: () -> Unit = {}) {
    parse(activity as Context, url, packageName, action)
  }

  @Throws(Exception::class)
  fun parseForResult(activity: Activity, url: String, requestCode: Int) {
    try {
      activity.startActivityForResult(handleIntent(url), requestCode)
    } catch (e: Throwable) {
      throw e
    }
  }

  @Throws(Exception::class)
  fun parse(fragment: Fragment, url: String) {
    try {
      fragment.startActivity(handleIntent(url))
    } catch (e: Throwable) {
      throw e
    }
  }

  @Throws(Exception::class)
  fun parseForResult(fragment: Fragment, url: String, requestCode: Int) {
    try {
      fragment.startActivityForResult(handleIntent(url), requestCode)
    } catch (e: Throwable) {
      throw e
    }
  }

  @Throws(URISyntaxException::class)
  fun handleIntent(url: String): Intent {
    val intent: Intent
    try {
      intent = Intent.parseUri(url, 0).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    } catch (e: Throwable) {
      throw e
    }
    return intent
  }
}

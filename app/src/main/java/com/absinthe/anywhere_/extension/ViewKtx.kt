package com.absinthe.anywhere_.extension

import android.view.View
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.absinthe.libraries.utils.extensions.addPaddingTop
import com.absinthe.libraries.utils.manager.SystemBarManager
import com.absinthe.libraries.utils.utils.UiUtils

fun View.addSystemBarPadding(
  addStatusBarPadding: Boolean = true,
  addNavigationBarPadding: Boolean = true
) {
  if (addStatusBarPadding) {
    addPaddingTop(UiUtils.getStatusBarHeight())
  }
  if (addNavigationBarPadding) {
    addPaddingBottom(SystemBarManager.navigationBarSize)
  }
}

fun View.addSystemBarPaddingAsync(
  addStatusBarPadding: Boolean = true,
  addNavigationBarPadding: Boolean = true
) {
  post {
    if (addStatusBarPadding) {
      addPaddingTop(UiUtils.getStatusBarHeight())
    }
    if (addNavigationBarPadding) {
      addPaddingBottom(SystemBarManager.navigationBarSize)
    }
  }
}

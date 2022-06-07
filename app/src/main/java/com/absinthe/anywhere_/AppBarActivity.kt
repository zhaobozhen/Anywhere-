package com.absinthe.anywhere_

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.AppBarLayout

abstract class AppBarActivity<T : ViewBinding> : BaseActivity<T>() {
  protected abstract fun getToolBar(): Toolbar
  protected abstract fun getAppBarLayout(): AppBarLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(getToolBar())
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    (root as ViewGroup).bringChildToFront(getAppBarLayout())
  }
}

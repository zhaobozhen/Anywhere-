package com.absinthe.anywhere_

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import rikka.material.widget.AppBarLayout

abstract class AppBarActivity<T : ViewBinding> : BaseActivity<T>() {
    protected abstract fun getToolBar(): Toolbar
    protected abstract fun getAppBarLayout(): AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppBar()
    }

    private fun initAppBar() {
        setAppBar(getAppBarLayout(), getToolBar())
        (root as ViewGroup).bringChildToFront(getAppBarLayout())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
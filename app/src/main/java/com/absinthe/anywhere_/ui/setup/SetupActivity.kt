package com.absinthe.anywhere_.ui.setup

import android.os.Bundle
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivitySetupBinding

class SetupActivity : AppBarActivity<ActivitySetupBinding>() {

  override fun setViewBinding() = ActivitySetupBinding.inflate(layoutInflater)

  override fun getToolBar() = binding.toolbar.toolBar

  override fun getAppBarLayout() = binding.toolbar.appBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    supportFragmentManager
      .beginTransaction()
      .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
      .replace(binding.fragmentContainerView.id, WelcomeFragment.newInstance())
      .commitNow()
  }

  override fun initView() {
    super.initView()
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
  }
}

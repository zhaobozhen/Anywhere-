package com.absinthe.anywhere_.ui.setup

import android.os.Bundle
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivitySetupBinding

class SetupActivity : BaseActivity() {

    private lateinit var binding: ActivitySetupBinding

    override fun setViewBinding() {
        isPaddingToolbar = true
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.toolbar.toolbar
    }

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

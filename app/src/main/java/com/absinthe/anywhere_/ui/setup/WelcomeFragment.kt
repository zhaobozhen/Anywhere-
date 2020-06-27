package com.absinthe.anywhere_.ui.setup

import android.view.View
import com.absinthe.anywhere_.BaseFragment
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(R.layout.fragment_welcome) {

    override fun initBinding(view: View): FragmentWelcomeBinding = FragmentWelcomeBinding.bind(view)

    override fun init() {
        setHasOptionsMenu(true)
        binding.btnWelcomeStart.setOnClickListener {
            requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.fragment_container_view, InitializeFragment.newInstance())
                    .commitNow()
        }
    }

    companion object {
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}
package com.absinthe.anywhere_.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = FragmentWelcomeBinding.inflate(inflater, container, false)
    setHasOptionsMenu(true)

    binding.btnWelcomeStart.setOnClickListener {
      requireActivity().supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
        .replace(R.id.fragment_container_view, InitializeFragment.newInstance())
        .commitNow()
    }

    return binding.root
  }

  companion object {
    @JvmStatic
    fun newInstance(): WelcomeFragment {
      return WelcomeFragment()
    }
  }
}

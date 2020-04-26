package com.absinthe.anywhere_.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.databinding.FragmentWelcomeBinding
import com.absinthe.anywhere_.utils.TimeRecorder

class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.btnWelcomeStart.setOnClickListener {
            (requireActivity() as MainActivity).viewModel.fragment.setValue(InitializeFragment.newInstance())
        }

        TimeRecorder.shouldRecord = false

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}
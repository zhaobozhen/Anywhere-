package com.absinthe.anywhere_.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment {

    static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentWelcomeBinding binding = FragmentWelcomeBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        binding.btnWelcomeStart.setOnClickListener(view1 ->
                MainActivity.getInstance().getViewModel().getFragment().setValue(InitializeFragment.newInstance()));

        return binding.getRoot();
    }
}
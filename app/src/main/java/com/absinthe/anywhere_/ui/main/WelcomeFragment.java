package com.absinthe.anywhere_.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding mBinding;

    static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentWelcomeBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        mBinding.btnWelcomeStart.setOnClickListener(view1 -> MainActivity.getInstance()
                .getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .replace(R.id.fragment_container_view, InitializeFragment.newInstance())
                .commitNow());

        return mBinding.getRoot();
    }
}
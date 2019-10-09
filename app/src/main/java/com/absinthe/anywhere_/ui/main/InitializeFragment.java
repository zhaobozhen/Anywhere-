package com.absinthe.anywhere_.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewmodel.InitializeViewModel;

public class InitializeFragment extends Fragment {

    private InitializeViewModel mViewModel;

    public static InitializeFragment newInstance() {
        return new InitializeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.initialize_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InitializeViewModel.class);
        // TODO: Use the ViewModel
    }

}

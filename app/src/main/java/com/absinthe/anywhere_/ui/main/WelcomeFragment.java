package com.absinthe.anywhere_.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.R;

public class WelcomeFragment extends Fragment {

    static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        setHasOptionsMenu(true);
        Button btnStart = view.findViewById(R.id.btn_welcome_start);
        btnStart.setOnClickListener(view1 -> MainActivity.getInstance()
                .getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .replace(R.id.fragment_container_view, InitializeFragment.newInstance())
                .commitNow());

        return view;
    }
}
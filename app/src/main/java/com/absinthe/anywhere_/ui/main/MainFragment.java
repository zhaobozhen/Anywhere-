package com.absinthe.anywhere_.ui.main;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AccessibilityUtil;
import com.absinthe.anywhere_.viewmodel.AnywhereObserver;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment implements LifecycleOwner {

    private static final int REQUEST_CODE = 1;
    private AnywhereObserver observer;
    private AnywhereViewModel mViewModel;
    private FloatingActionButton fab;
    private TextView tvPackage, tvClass;
    private Context mContext;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = getView().findViewById(R.id.fab);
        tvPackage = getView().findViewById(R.id.tv_package);
        tvClass = getView().findViewById(R.id.tv_class);
        mContext = getContext();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);
        // TODO: Use the ViewModel
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOverlayPermission();
            }
        });
        final Observer<String> packageNameObserver =new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvPackage.setText(s);
            }
        };
        final Observer<String> classNameObserver =new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvClass.setText(s);
            }
        };
        mViewModel.getPackageName().observe(this, packageNameObserver);
        mViewModel.getClassName().observe(this, classNameObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mViewModel.getPackageName().setValue(bundle.getString("packageName"));
            mViewModel.getClassName().setValue(bundle.getString("className"));
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                startActivityForResult(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName())),
                        REQUEST_CODE
                );
                Toast.makeText(mContext, "请先授予 \"Anywhere-\" 悬浮窗权限", Toast.LENGTH_LONG).show();
            } else {
                startCollector();
            }
        }
    }

    private void startCollector() {
        if (AccessibilityUtil.checkAccessibility(mContext)) {
            Intent intent = new Intent(mContext, CollectorService.class);
            intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
            Toast.makeText(getContext(), "已开启Collector", Toast.LENGTH_SHORT).show();
            mContext.startService(intent);
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Settings.canDrawOverlays(getActivity())) {
            startCollector();
        }
    }

}

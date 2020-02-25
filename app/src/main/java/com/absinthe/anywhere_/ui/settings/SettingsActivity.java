package com.absinthe.anywhere_.ui.settings;

import android.os.Bundle;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding;

public class SettingsActivity extends BaseActivity {

    private static SettingsActivity sInstance;
    private ActivitySettingsBinding mBinding;

    public static SettingsActivity getInstance() {
        return sInstance;
    }

    public static boolean isAvailable() {
        return sInstance != null;
    }

    @Override
    protected void setViewBinding() {
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = mBinding.toolbar.toolbar;
    }

    @Override
    protected boolean isPaddingToolbar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }
}

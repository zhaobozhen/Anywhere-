package com.absinthe.anywhere_.ui.settings;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding mBinding;

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
}

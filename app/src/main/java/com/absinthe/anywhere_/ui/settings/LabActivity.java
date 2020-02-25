package com.absinthe.anywhere_.ui.settings;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.databinding.ActivityLabBinding;

public class LabActivity extends BaseActivity {

    private ActivityLabBinding mBinding;

    @Override
    protected void setViewBinding() {
        mBinding = ActivityLabBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = mBinding.toolbar.toolbar;
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

}

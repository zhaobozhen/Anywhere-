package com.absinthe.anywhere_.ui.settings;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;

public class LabActivity extends BaseActivity {

    @Override
    protected void setViewBinding() {
        setContentView(R.layout.activity_lab);
    }

    @Override
    protected void setToolbar() {
        mToolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

}

package com.absinthe.anywhere_.ui.settings;

import android.os.Bundle;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;

public class SettingsActivity extends BaseActivity {

    private static SettingsActivity sInstance;

    public static SettingsActivity getInstance() {
        return sInstance;
    }

    public static boolean isAvailable() {
        return sInstance != null;
    }

    @Override
    protected void setViewBinding() {
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void setToolbar() {
        mToolbar = findViewById(R.id.toolbar);
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

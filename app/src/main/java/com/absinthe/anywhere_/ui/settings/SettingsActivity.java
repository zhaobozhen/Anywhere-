package com.absinthe.anywhere_.ui.settings;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sInstance = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, SettingsFragment.newInstance())
                    .commit();
        }

    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }
}

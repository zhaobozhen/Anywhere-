package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;

public class GiftActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!BuildConfig.DEBUG) {
            finish();
        }
    }
}

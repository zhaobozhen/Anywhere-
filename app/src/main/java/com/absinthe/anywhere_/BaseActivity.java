package com.absinthe.anywhere_;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (UiUtils.isDarkMode(this)) {
            UiUtils.clearLightStatusBar(getWindow().getDecorView());
        }
    }
}

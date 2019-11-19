package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        Window window = getWindow();
        int transparent = getResources().getColor(R.color.transparent);
        int nav_color = getResources().getColor(R.color.navigationColorNormal);
        window.setNavigationBarColor(nav_color);

        if (UiUtils.isDarkMode(this)) {
            UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
        } else {
            window.setStatusBarColor(transparent);
        }
    }
}

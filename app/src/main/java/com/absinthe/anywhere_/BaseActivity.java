package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        Window window = getWindow();
        int nav_color = getResources().getColor(R.color.navigationColorNormal);

        if (GlobalValues.sBackgroundUri.isEmpty()) {
            window.setStatusBarColor(nav_color);
            window.setNavigationBarColor(nav_color);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (UiUtils.isDarkMode(this)) {
            UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
        }
    }
}

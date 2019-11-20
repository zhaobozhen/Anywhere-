package com.absinthe.anywhere_;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.StatusBarUtil;
import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        int nav_color = getResources().getColor(R.color.navigationColorNormal);

        if (GlobalValues.sBackgroundUri.isEmpty() || !(this instanceof MainActivity)) {
            StatusBarUtil.setColorNoTranslucent(this, nav_color);
        }

        if (UiUtils.isDarkMode(this)) {
            UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
        }
    }
}

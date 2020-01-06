package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.StatusBarUtil;
import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(this.getClass().getSimpleName(), "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        int nav_color = getResources().getColor(R.color.navigationColorNormal);

        if ((GlobalValues.sBackgroundUri.isEmpty() && !GlobalValues.sIsMd2Toolbar) || !(this instanceof MainActivity)) {
            StatusBarUtil.setColorNoTranslucent(this, nav_color);
        }

        if (UiUtils.isDarkMode(this)) {
            if (GlobalValues.sBackgroundUri.isEmpty() || !(this instanceof MainActivity)) {
                UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
            } else {
                UiUtils.setActionBarTitle(this, getSupportActionBar());
            }
        }
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

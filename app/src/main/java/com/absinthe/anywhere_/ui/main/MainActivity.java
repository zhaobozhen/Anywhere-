package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.ImageUtils;
import com.absinthe.anywhere_.utils.SPUtils;

public class MainActivity extends AppCompatActivity {
    private MainFragment mainFragment;
    private static Fragment curFragment;
    private static final String TAG = "MainActivity";
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        initView();
        instance = this;

        boolean isFirstLaunch = SPUtils.getBoolean(this, ConstUtil.SP_KEY_FIRST_LAUNCH);
        if (isFirstLaunch) {
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mainFragment = MainFragment.newInstance();
            getAnywhereIntent(getIntent());

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commitNow();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getAnywhereIntent(intent);
    }

    private void initView() {
        String backgroundUri = SPUtils.getString(this, ConstUtil.SP_KEY_CHANGE_BACKGROUND);
        ImageView ivBackground = findViewById(R.id.iv_background);
        if (!backgroundUri.isEmpty()) {
            ImageUtils.setActionBarTransparent(this);
            ImageUtils.loadBackgroundPic(this, ivBackground);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView ivBackground = findViewById(R.id.iv_background);

        ImageUtils.loadBackgroundPic(this, ivBackground);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String actionBarType = SPUtils.getString(this, ConstUtil.SP_KEY_ACTION_BAR_TYPE);
        Log.d(TAG, "onPrepareOptionsMenu: actionBarType = " + actionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            switch (actionBarType) {
                case "":
                case ConstUtil.ACTION_BAR_TYPE_LIGHT:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_light);
                    break;
                case ConstUtil.ACTION_BAR_TYPE_DARK:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_dark);
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static Fragment getCurFragment() {
        return curFragment;
    }

    public static void setCurFragment(Fragment fragment) {
        curFragment = fragment;
    }

    public void setMainFragment(MainFragment fragment) {
        mainFragment = fragment;
    }

    private void getAnywhereIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String packageName = intent.getStringExtra(ConstUtil.INTENT_EXTRA_PACKAGE_NAME);
        String className = intent.getStringExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME);
        int classNameType = intent.getIntExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME_TYPE, ConstUtil.SHORT_CLASS_NAME_TYPE);
        String shortcutEditUrl = intent.getStringExtra("shortcutEditUrl");

        Bundle bundle = new Bundle();
        if (GlobalValues.sWorkingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
            if (shortcutEditUrl != null) {
                bundle.putString("shortcutEditUrl", shortcutEditUrl);
            }
        } else {
            if (packageName == null || className == null) {
                return;
            }

            Log.d(TAG, "classNameType = " + classNameType);
            Log.d(TAG, "className = " + className);
            Log.d(TAG, "packageName = " + packageName);

            bundle.putString(ConstUtil.BUNDLE_PACKAGE_NAME, packageName);
            bundle.putString(ConstUtil.BUNDLE_CLASS_NAME, className);
            bundle.putInt(ConstUtil.BUNDLE_CLASS_NAME_TYPE, classNameType);
        }

        mainFragment.setArguments(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "curFragment = " + curFragment);

        if (requestCode == ConstUtil.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Log.d(TAG, "REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
            if (curFragment instanceof MainFragment) {
                if (mainFragment == null) {
                    mainFragment = (MainFragment) curFragment;
                }
                mainFragment.checkWorkingPermission();
            } else if (curFragment instanceof InitializeFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        InitializeFragment.getViewModel().getIsOverlay().setValue(Boolean.TRUE);
                    }
                }
            }
        } else if (requestCode == ConstUtil.REQUEST_CODE_SHIZUKU_PERMISSION) {
            Log.d(TAG, "REQUEST_CODE_SHIZUKU_PERMISSION");
            if (curFragment instanceof InitializeFragment) {
                InitializeFragment.getViewModel().getIsShizuku().setValue(Boolean.TRUE);
            }
        }
    }
}

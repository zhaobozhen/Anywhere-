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

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainFragment mainFragment;
    private static Fragment curFragment;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        initView();
        instance = this;

        if (GlobalValues.sIsFirstLaunch) {
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
        ImageView ivBackground = findViewById(R.id.iv_background);
        if (!GlobalValues.sBackgroundUri.isEmpty()) {
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
        Log.d(TAG, "onPrepareOptionsMenu: actionBarType = " + GlobalValues.sActionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            switch (GlobalValues.sActionBarType) {
                case "":
                case Const.ACTION_BAR_TYPE_LIGHT:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_light);
                    break;
                case Const.ACTION_BAR_TYPE_DARK:
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

        String packageName = intent.getStringExtra(Const.INTENT_EXTRA_PACKAGE_NAME);
        String className = intent.getStringExtra(Const.INTENT_EXTRA_CLASS_NAME);
        int classNameType = intent.getIntExtra(Const.INTENT_EXTRA_CLASS_NAME_TYPE, Const.SHORT_CLASS_NAME_TYPE);
        String shortcutEditUrl = intent.getStringExtra("shortcutEditUrl");

        Bundle bundle = new Bundle();
        if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
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

            bundle.putString(Const.BUNDLE_PACKAGE_NAME, packageName);
            bundle.putString(Const.BUNDLE_CLASS_NAME, className);
            bundle.putInt(Const.BUNDLE_CLASS_NAME_TYPE, classNameType);
        }

        mainFragment.setArguments(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "curFragment = " + curFragment);

        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
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
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            Log.d(TAG, "REQUEST_CODE_SHIZUKU_PERMISSION");
            if (curFragment instanceof InitializeFragment) {
                InitializeFragment.getViewModel().getIsShizuku().setValue(Boolean.TRUE);
            }
        }
    }
}

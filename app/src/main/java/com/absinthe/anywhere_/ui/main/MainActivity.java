package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.StatusBarUtil;
import com.absinthe.anywhere_.utils.UiUtils;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private MainFragment mainFragment;
    private static Fragment curFragment;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        instance = this;

        if (GlobalValues.sIsFirstLaunch) {
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mainFragment = MainFragment.newInstance();
            getAnywhereIntent(getIntent());

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container, mainFragment)
                        .commitNow();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Settings.setTheme(GlobalValues.sDarkMode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getAnywhereIntent(intent);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            UiUtils.loadBackgroundPic(this, binding.ivBackground);
            UiUtils.setActionBarTransparent(this);
            UiUtils.setMargins(findViewById(R.id.cl_main), StatusBarUtil.getStatusBarHeight(this),
                    StatusBarUtil.getNavigationBarHeight(this));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        UiUtils.loadBackgroundPic(this, binding.ivBackground);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        LogUtil.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            switch (GlobalValues.sActionBarType) {
                case "":
                case Const.ACTION_BAR_TYPE_LIGHT:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_light);
                    menu.findItem(R.id.toolbar_sort).setIcon(R.drawable.ic_filter_list_light);
                    menu.findItem(R.id.toolbar_delete).setIcon(R.drawable.ic_delete_light);
                    menu.findItem(R.id.toolbar_done).setIcon(R.drawable.ic_done_light);
                    break;
                case Const.ACTION_BAR_TYPE_DARK:
                    if (UiUtils.isDarkMode(this)) {
                        menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_light);
                        menu.findItem(R.id.toolbar_sort).setIcon(R.drawable.ic_filter_list_light);
                        menu.findItem(R.id.toolbar_delete).setIcon(R.drawable.ic_delete_light);
                        menu.findItem(R.id.toolbar_done).setIcon(R.drawable.ic_done_light);
                    } else {
                        menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_dark);
                        menu.findItem(R.id.toolbar_sort).setIcon(R.drawable.ic_filter_list_dark);
                        menu.findItem(R.id.toolbar_delete).setIcon(R.drawable.ic_delete_dark);
                        menu.findItem(R.id.toolbar_done).setIcon(R.drawable.ic_done_dark);
                    }
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void setCurFragment(Fragment fragment) {
        curFragment = fragment;
    }

    public void setMainFragment(MainFragment fragment) {
        mainFragment = fragment;
    }

    private void getAnywhereIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        } else {
            LogUtil.d("Received Url =", uri.toString());
        }
        String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
        String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
        String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);
        LogUtil.d("Url param =", param1, param2, param3);

        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_EXTRA_PARAM_1, param1);
        bundle.putString(Const.INTENT_EXTRA_PARAM_2, param2);
        bundle.putString(Const.INTENT_EXTRA_PARAM_3, param3);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }
        mainFragment.setArguments(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("curFragment =" + curFragment);

        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            LogUtil.d("REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
            if (curFragment instanceof MainFragment) {
                if (mainFragment == null) {
                    mainFragment = (MainFragment) curFragment;
                }
                if (resultCode == RESULT_OK) {
                    mainFragment.checkWorkingPermission();
                }
            } else if (curFragment instanceof InitializeFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (android.provider.Settings.canDrawOverlays(this)) {
                        InitializeFragment.getViewModel().getIsOverlay().setValue(Boolean.TRUE);
                    }
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            LogUtil.d("REQUEST_CODE_SHIZUKU_PERMISSION");
            if (curFragment instanceof InitializeFragment) {
                InitializeFragment.getViewModel().getIsShizuku().setValue(Boolean.TRUE);
            }
        }
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

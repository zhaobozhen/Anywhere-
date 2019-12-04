package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import jonathanfinerty.once.Once;

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

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE) &&
                SPUtils.getBoolean(this, Const.SP_KEY_FIRST_LAUNCH, true)) {
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mainFragment = MainFragment.newInstance();
            getAnywhereIntent(getIntent());

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
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
            binding.ivBackground.setVisibility(View.VISIBLE);
            UiUtils.setActionBarTransparent(this);
            UiUtils.setAdaptiveActionBarTitleColor(this, getSupportActionBar(), UiUtils.getActionBarTitle());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        UiUtils.loadBackgroundPic(this, binding.ivBackground);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_LIGHT) || ( UiUtils.isDarkMode(this) && GlobalValues.sBackgroundUri.isEmpty() )) {
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_settings), R.color.white);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_sort), R.color.white);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_delete), R.color.white);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_done), R.color.white);
            } else {
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_settings), R.color.black);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_sort), R.color.black);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_delete), R.color.black);
                UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_done), R.color.black);
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
            Logger.d("Received Url =", uri.toString());
        }
        String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
        String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
        String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);
        Logger.d("Url param =", param1, param2, param3);

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
        Logger.d("curFragment =" + curFragment);

        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Logger.d("REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
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
            Logger.d("REQUEST_CODE_SHIZUKU_PERMISSION");
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

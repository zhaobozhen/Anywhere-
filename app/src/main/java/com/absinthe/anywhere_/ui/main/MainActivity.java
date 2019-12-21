package com.absinthe.anywhere_.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.databinding.ActivityMainMd2Binding;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import java.util.Objects;

import jonathanfinerty.once.Once;

public class MainActivity extends BaseActivity {
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;
    private static Fragment sCurFragment;

    private MainFragment mMainFragment;
    private boolean isMd2Theme = false;

    private ImageView ivBackground;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMd2Theme = SPUtils.getBoolean(this, Const.PREF_MD2_TOOLBAR, false);

        if (isMd2Theme) {
            ActivityMainMd2Binding binding2 = DataBindingUtil.setContentView(this, R.layout.activity_main_md2);
            ivBackground = binding2.ivBackground;
            toolbar = binding2.toolbar;
        } else {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            ivBackground = binding.ivBackground;
            toolbar = binding.toolbar;
        }
        initView();
        sInstance = this;

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE) &&
                SPUtils.getBoolean(this, Const.PREF_FIRST_LAUNCH, true)) {
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mMainFragment = MainFragment.newInstance();
            getAnywhereIntent(getIntent());

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                        .replace(R.id.container, mMainFragment)
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        UiUtils.loadBackgroundPic(this, ivBackground);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_LIGHT) || ( UiUtils.isDarkMode(this) && GlobalValues.sBackgroundUri.isEmpty() )) {
                tintToolbarIcon(menu, Const.ACTION_BAR_TYPE_LIGHT);
            } else {
                tintToolbarIcon(menu, Const.ACTION_BAR_TYPE_DARK);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    public static void setCurFragment(Fragment fragment) {
        sCurFragment = fragment;
    }

    public void setMainFragment(MainFragment fragment) {
        mMainFragment = fragment;
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void initView() {
        setSupportActionBar(toolbar);

        if (isMd2Theme) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
        }

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            UiUtils.loadBackgroundPic(this, ivBackground);
            ivBackground.setVisibility(View.VISIBLE);
            UiUtils.setActionBarTransparent(this);
            UiUtils.setAdaptiveActionBarTitleColor(this, getSupportActionBar(), UiUtils.getActionBarTitle());
        }
    }

    private void getAnywhereIntent(Intent intent) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equals(Intent.ACTION_VIEW)) {
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

                if (mMainFragment == null) {
                    mMainFragment = MainFragment.newInstance();
                }
                mMainFragment.setArguments(bundle);
            } else if (action.equals(Intent.ACTION_SEND)) {
                String sharing = intent.getStringExtra(Intent.EXTRA_TEXT);

                Bundle bundle = new Bundle();
                bundle.putString(Const.INTENT_EXTRA_PARAM_1, TextUtils.parseUrlFromSharingText(sharing));
                bundle.putString(Const.INTENT_EXTRA_PARAM_2, "");
                bundle.putString(Const.INTENT_EXTRA_PARAM_3, "");

                if (mMainFragment == null) {
                    mMainFragment = MainFragment.newInstance();
                }
                mMainFragment.setArguments(bundle);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("curFragment =" + sCurFragment);

        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Logger.d("REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
            if (sCurFragment instanceof MainFragment) {
                if (mMainFragment == null) {
                    mMainFragment = (MainFragment) sCurFragment;
                }
                if (resultCode == RESULT_OK) {
                    mMainFragment.checkWorkingPermission();
                }
            } else if (sCurFragment instanceof InitializeFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (android.provider.Settings.canDrawOverlays(this)) {
                        InitializeFragment.getViewModel().getIsOverlay().setValue(Boolean.TRUE);
                    }
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            Logger.d("REQUEST_CODE_SHIZUKU_PERMISSION");
            if (sCurFragment instanceof InitializeFragment) {
                InitializeFragment.getViewModel().getIsShizuku().setValue(Boolean.TRUE);
            }
        }
    }

    private void tintToolbarIcon(Menu menu, String type) {
        int colorRes;
        if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
            colorRes = R.color.black;
        } else {
            colorRes = R.color.white;
        }

        UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_settings), colorRes);
        UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_sort), colorRes);
        UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_delete), colorRes);
        UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_done), colorRes);
        UiUtils.tintMenuIcon(this, menu.findItem(R.id.toolbar_done), colorRes);

        final Drawable home = getResources().getDrawable(R.drawable.ic_menu);
        DrawableCompat.setTint(home, getResources().getColor(colorRes));
        Objects.requireNonNull(
                getSupportActionBar()).setHomeAsUpIndicator(home);
    }
}

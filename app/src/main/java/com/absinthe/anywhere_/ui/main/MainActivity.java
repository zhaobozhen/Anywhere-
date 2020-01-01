package com.absinthe.anywhere_.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.PageListAdapter;
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
import com.absinthe.anywhere_.view.RoundLinerLayoutNormal;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;

import java.util.Objects;

import jonathanfinerty.once.Once;

public class MainActivity extends BaseActivity {
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;
    private static Fragment sCurFragment;

    private MainFragment mMainFragment;

    public ImageView mIvBackground;
    public RoundLinerLayoutNormal mToolbarContainer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnywhereViewModel viewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);

        if (GlobalValues.sIsMd2Toolbar) {
            ActivityMainMd2Binding binding2 = DataBindingUtil.setContentView(this, R.layout.activity_main_md2);
            if (!GlobalValues.sBackgroundUri.isEmpty()) {
                mIvBackground = (ImageView) Objects.requireNonNull(binding2.stubBg.getViewStub()).inflate();
            }
            mToolbar = binding2.toolbar;
            mToolbarContainer = binding2.toolbarContainer;
            viewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> initDrawer(binding2.drawer));
        } else {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            if (!GlobalValues.sBackgroundUri.isEmpty()) {
                mIvBackground = (ImageView) Objects.requireNonNull(binding.stubBg.getViewStub()).inflate();
            }
            mToolbar = binding.toolbar;
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToggle != null) {
            mToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UiUtils.loadBackgroundPic(this, mIvBackground);
        if (mToggle != null) {
            mToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (!GlobalValues.sIsMd2Toolbar) {
            if (menu.findItem(R.id.toolbar_settings) != null) {
                if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_LIGHT)
                        || (UiUtils.isDarkMode(this) && GlobalValues.sBackgroundUri.isEmpty())) {
                    tintToolbarIcon(menu, Const.ACTION_BAR_TYPE_LIGHT);
                } else {
                    tintToolbarIcon(menu, Const.ACTION_BAR_TYPE_DARK);
                }
            }
        } else {
            if (UiUtils.isDarkMode(this)) {
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
        setSupportActionBar(mToolbar);

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            UiUtils.loadBackgroundPic(this, mIvBackground);
            UiUtils.setActionBarTransparent(this);
            UiUtils.setAdaptiveActionBarTitleColor(this, getSupportActionBar(), UiUtils.getActionBarTitle());
        }
    }

    private void initDrawer(DrawerLayout drawer) {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            mToggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            drawer.addDrawerListener(mToggle);
            mToggle.syncState();
        }

        RecyclerView recyclerView = drawer.findViewById(R.id.rv_pages);
        PageListAdapter adapter = new PageListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        drawer.findViewById(R.id.ib_add).setOnClickListener(v -> adapter.addPage());
    }

    private void getAnywhereIntent(Intent intent) {
        String action = intent.getAction();

        Logger.d("action = ", action);

        if (action != null) {
            if (action.equals(Intent.ACTION_SEND)) {
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
        } else {
            Uri uri = intent.getData();

            if (uri == null) {
                return;
            } else {
                Logger.d("Received Url =", uri.toString());
            }

            String host = uri.getHost();
            String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
            String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
            String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);
            Logger.d("Url param =", param1, param2, param3);

            Bundle bundle = new Bundle();
            bundle.putString(Const.INTENT_EXTRA_URI_HOST, host);
            bundle.putString(Const.INTENT_EXTRA_PARAM_1, param1);
            bundle.putString(Const.INTENT_EXTRA_PARAM_2, param2);
            bundle.putString(Const.INTENT_EXTRA_PARAM_3, param3);

            if (mMainFragment == null) {
                mMainFragment = MainFragment.newInstance();
            }
            mMainFragment.setArguments(bundle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle != null && mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        if (mToggle != null) {
            if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
                mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
            } else {
                mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
            }
        }
    }
}

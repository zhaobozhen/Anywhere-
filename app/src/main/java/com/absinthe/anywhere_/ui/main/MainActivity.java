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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.page.PageListAdapter;
import com.absinthe.anywhere_.adapter.page.PageTitleNode;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.databinding.ActivityMainMd2Binding;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.List;
import java.util.Objects;

import jonathanfinerty.once.Once;

public class MainActivity extends BaseActivity {
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;
    private static Fragment sCurFragment;

    private MainFragment mMainFragment;
    private AnywhereViewModel mViewModel;

    public ImageView mIvBackground;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;

    public static MainActivity getInstance() {
        return sInstance;
    }

    public AnywhereViewModel getViewModel() {
        return mViewModel;
    }

    public static void setCurFragment(Fragment fragment) {
        sCurFragment = fragment;
    }

    public void setMainFragment(MainFragment fragment) {
        mMainFragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sInstance = this;
        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);
        setLayout();
        initView();
        initObserver();

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
        UiUtils.loadBackgroundPic(this, mIvBackground);
        if (mToggle != null) {
            mToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_LIGHT)
                || (UiUtils.isDarkMode(this) && GlobalValues.sBackgroundUri.isEmpty())
                || (UiUtils.isDarkMode(this) && GlobalValues.sIsMd2Toolbar)) {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_LIGHT);
        } else {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_DARK);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setLayout() {
        if (GlobalValues.sIsMd2Toolbar) {
            ActivityMainMd2Binding binding2 = DataBindingUtil.setContentView(this, R.layout.activity_main_md2);
            if (!GlobalValues.sBackgroundUri.isEmpty()) {
                mIvBackground = (ImageView) Objects.requireNonNull(binding2.stubBg.getViewStub()).inflate();
            }
            mToolbar = binding2.toolbar;
            mDrawer = binding2.drawer;
        } else {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            if (!GlobalValues.sBackgroundUri.isEmpty()) {
                mIvBackground = (ImageView) Objects.requireNonNull(binding.stubBg.getViewStub()).inflate();
            }
            mToolbar = binding.toolbar;
            mDrawer = binding.drawer;
        }
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (GlobalValues.sIsPages) {
                mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
                actionBar.setDisplayHomeAsUpEnabled(true);
                mDrawer.addDrawerListener(mToggle);
                mToggle.syncState();

                AnywhereApplication.sRepository
                        .getAllAnywhereEntities()
                        .observe(this, anywhereEntities -> initDrawer(mDrawer));
            } else {
                actionBar.setHomeButtonEnabled(false);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            UiUtils.loadBackgroundPic(this, mIvBackground);
            UiUtils.setActionBarTransparent(this);
            UiUtils.setAdaptiveActionBarTitleColor(this, getSupportActionBar(), UiUtils.getActionBarTitle());
        }
    }

    private void initDrawer(DrawerLayout drawer) {
        RecyclerView recyclerView = drawer.findViewById(R.id.rv_pages);

        PageListAdapter adapter = new PageListAdapter();
        AnywhereApplication.sRepository.getAllPageEntities().observe(this, pageEntities -> {
            if (pageEntities != null) {
                if (adapter.getItemCount() == 0) {
                    for (PageEntity pe : pageEntities) {
                        adapter.addData(mViewModel.getEntity(pe.getTitle()));
                    }
                } else {
                    if (pageEntities.size() > adapter.getItemCount() / 2) { //Item count == title page + clip page
                        adapter.addData(mViewModel.getEntity(pageEntities.get(pageEntities.size() - 1).getTitle()));
                    } else if (pageEntities.size() < adapter.getItemCount() / 2) {
                        for (PageEntity pe : pageEntities) {
                            for (BaseNode node : adapter.getData()) {
                                if (node instanceof PageTitleNode) {

                                }
                            }
                        }
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) Objects.requireNonNull(
                recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        drawer.findViewById(R.id.ib_add).setOnClickListener(v -> {
            List<PageEntity> list = AnywhereApplication.sRepository.getAllPageEntities().getValue();
            if (list != null) {
                if (list.size() != 0) {
                    int size = list.size();
                    PageEntity pe = new PageEntity("Page " + (size + 1), size + 1, System.currentTimeMillis() + "");
                    AnywhereApplication.sRepository.insertPage(pe);
                } else {
                    PageEntity pe = new PageEntity(AnywhereType.DEFAULT_CATEGORY, 1, System.currentTimeMillis() + "");
                    AnywhereApplication.sRepository.insertPage(pe);
                }
            }
        });
    }

    private void initObserver() {
        mViewModel.getBackground().observe(this, s -> {
            if (!s.isEmpty()) {
                UiUtils.loadBackgroundPic(sInstance, mIvBackground);
                UiUtils.setActionBarTransparent(MainActivity.getInstance());
                UiUtils.setAdaptiveActionBarTitleColor(sInstance, getSupportActionBar(), UiUtils.getActionBarTitle());
            }
            GlobalValues.setsBackgroundUri(s);
        });

        mViewModel.getBackground().setValue(GlobalValues.sBackgroundUri);
    }

    private void getAnywhereIntent(Intent intent) {
        String action = intent.getAction();

        Logger.d("action = ", action);

        if (action == null || action.equals(Intent.ACTION_VIEW)) {
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

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

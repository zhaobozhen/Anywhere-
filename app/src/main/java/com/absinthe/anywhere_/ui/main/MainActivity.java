package com.absinthe.anywhere_.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.page.MainPageAdapter;
import com.absinthe.anywhere_.adapter.page.PageListAdapter;
import com.absinthe.anywhere_.adapter.page.PageTitleNode;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.databinding.ActivityMainMd2Binding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.ui.list.AppListActivity;
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.FirebaseUtil;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.AnywhereEditor;
import com.absinthe.anywhere_.view.Editor;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jonathanfinerty.once.Once;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends BaseActivity {
    @SuppressLint("StaticFieldLeak")
    private static MainActivity sInstance;
    private static Fragment sCurFragment;

    private MainFragment mMainFragment;
    private AnywhereViewModel mViewModel;
    private FirebaseAnalytics mFirebaseAnalytics;
    private MainPageAdapter mAdapter;

    /* View */
    public ImageView mIvBackground;
    public SpeedDialView mFab;

    private Toolbar mToolbar;
    private ViewPager2 mViewPager;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;

    private boolean observed = false;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setLayout();
        initView();

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE) &&
                SPUtils.getBoolean(this, Const.PREF_FIRST_LAUNCH, true)) {
            mFab.setVisibility(View.GONE);
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            initFab();
            initObserver();
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
            mViewPager = binding2.viewPager;
            mDrawer = binding2.drawer;
            mFab = binding2.fab;
        } else {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            if (!GlobalValues.sBackgroundUri.isEmpty()) {
                mIvBackground = (ImageView) Objects.requireNonNull(binding.stubBg.getViewStub()).inflate();
            }
            mToolbar = binding.toolbar;
            mViewPager = binding.viewPager;
            mDrawer = binding.drawer;
            mFab = binding.fab;
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

        mAdapter = new MainPageAdapter(this);
        mViewPager.setAdapter(mAdapter);
        if (!GlobalValues.sIsPages) {
            mViewPager.setUserInputEnabled(false);
        }
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                GlobalValues.setsCategory(Objects.requireNonNull(
                        mAdapter.getList().get(position).getValue()).get(0).getCategory(), position);
                super.onPageSelected(position);
            }
        });
        mViewPager.setPageTransformer((view, position) -> {
            if (position < -1 || position > 1) {
                view.setAlpha(0);
            }
            else if (position <= 0 || position <= 1) {
                // Calculate alpha. Position is decimal in [-1,0] or [0,1]
                float alpha = (position <= 0) ? position + 1 : 1 - position;
                view.setAlpha(alpha);
            }
            else if (position == 0) {
                view.setAlpha(1);
            }
        });
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
        mViewModel.getWorkingMode().observe(this, s -> {
            GlobalValues.setsWorkingMode(s);
            UiUtils.setActionBarTitle(this, getSupportActionBar());
        });
        mViewModel.getWorkingMode().setValue(GlobalValues.sWorkingMode);
        mViewModel.getCommand().observe(this, CommandUtils::execCmd);
        mViewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> {
            if (observed) {
                setupLists(mAdapter);
            } else {
                observed = true;
            }
        });
        AnywhereApplication.sRepository.getAllPageEntities().observe(this, pageEntities -> {
            if (observed) {
                setupLists(mAdapter);
            } else {
                observed = true;
            }
        });
    }

    public void initFab() {
        mFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_url_scheme, R.drawable.ic_url_scheme)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.btn_url_scheme))
                .setLabelClickable(false)
                .create());
        mFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_activity_list, R.drawable.ic_activity_list)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.btn_activity_list))
                .setLabelClickable(false)
                .create());
        mFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_qr_code_collection, R.drawable.ic_qr_code)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.btn_qr_code_collection))
                .setLabelClickable(false)
                .create());
        mFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_collector, R.drawable.ic_logo)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.ib_collector_todo))
                .setLabelClickable(false)
                .create());
        mFab.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.fab_url_scheme:
                    MainActivity.getInstance().getViewModel().setUpUrlScheme("");
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_url_scheme", "click_fab_url_scheme");
                    break;
                case R.id.fab_activity_list:
                    startActivity(new Intent(this, AppListActivity.class));
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_activity_list", "click_fab_activity_list");
                    break;
                case R.id.fab_collector:
                    MainActivity.getInstance().getViewModel().checkWorkingPermission(MainActivity.getInstance());
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_collector", "click_fab_collector");
                    break;
                case R.id.fab_qr_code_collection:
                    startActivity(new Intent(this, QRCodeCollectionActivity.class));
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_qr_code_collection", "click_fab_qr_code_collection");
                    break;
                default:
                    return false;
            }
            mFab.close();
            return true;
        });

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE)) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.fab)
                    .setPrimaryText(R.string.first_launch_guide_title)
                    .setBackgroundColour(getResources().getColor(R.color.colorAccent))
                    .show();
            Once.markDone(OnceTag.FAB_GUIDE);
        }
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

            if (param1 != null && param2 != null && param3 != null) {
                if (param2.isEmpty() && param3.isEmpty()) {
                    mViewModel.setUpUrlScheme(param1);
                } else {
                    String appName;
                    appName = TextUtils.getAppName(this, param1);

                    int exported = 0;
                    if (UiUtils.isActivityExported(this, new ComponentName(param1,
                            param2.charAt(0) == '.' ? param1 + param2 : param2))) {
                        exported = 100;
                    }

                    AnywhereEntity ae = AnywhereEntity.Builder();
                    ae.setAppName(appName);
                    ae.setParam1(param1);
                    ae.setParam2(param2);
                    ae.setParam3(param3);
                    ae.setType(AnywhereType.ACTIVITY + exported);

                    Editor editor = new AnywhereEditor(MainActivity.getInstance())
                            .item(ae)
                            .isEditorMode(false)
                            .isShortcut(false)
                            .build();
                    editor.show();
                }
            }
        } else if (action.equals(Intent.ACTION_SEND)) {
            String sharing = intent.getStringExtra(Intent.EXTRA_TEXT);
            mViewModel.setUpUrlScheme(sharing);
        }
    }

    private void setupLists(MainPageAdapter adapter) {
        List<MutableLiveData<List<AnywhereEntity>>> lists = new ArrayList<>();
        List<PageEntity> pageEntityList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        List<AnywhereEntity> anywhereEntityList = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();

        if (pageEntityList != null && anywhereEntityList != null) {
            for (PageEntity pe : pageEntityList) {
                MutableLiveData<List<AnywhereEntity>> liveData = new MutableLiveData<>();
                List<AnywhereEntity> list = new ArrayList<>();

                for (AnywhereEntity ae : anywhereEntityList) {
                    if (pe.getTitle().equals(ae.getCategory())) {
                        list.add(ae);
                    }
                }

                if (list.size() > 0) {
                    liveData.setValue(list);
                    lists.add(liveData);
                }
            }
        }

        adapter.setList(lists);
        mViewPager.setUserInputEnabled(lists.size() > 1);
        mViewPager.setCurrentItem(GlobalValues.sCurrentPage, false);
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
                    mViewModel.checkWorkingPermission(this);
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
}

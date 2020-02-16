package com.absinthe.anywhere_.ui.main;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.ItemTouchCallBack;
import com.absinthe.anywhere_.adapter.page.PageListAdapter;
import com.absinthe.anywhere_.adapter.page.PageTitleNode;
import com.absinthe.anywhere_.adapter.page.PageTitleProvider;
import com.absinthe.anywhere_.databinding.ActivityMainBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.ui.list.AppListActivity;
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.ClipboardUtil;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.FirebaseUtil;
import com.absinthe.anywhere_.utils.ListUtils;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.absinthe.anywhere_.view.editor.AnywhereEditor;
import com.absinthe.anywhere_.view.editor.Editor;
import com.absinthe.anywhere_.view.FabBuilder;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import jonathanfinerty.once.Once;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends BaseActivity {
    public ActivityMainBinding mBinding;

    private static MainActivity sInstance;
    private static boolean isPageInit = false;

    private MainFragment mCurrFragment;
    private AnywhereViewModel mViewModel;
    private FirebaseAnalytics mFirebaseAnalytics;

    private ActionBarDrawerToggle mToggle;
    private ItemTouchHelper mItemTouchHelper;

    public static MainActivity getInstance() {
        return sInstance;
    }

    public void setCurrFragment(MainFragment mCurrFragment) {
        this.mCurrFragment = mCurrFragment;
    }

    public MainFragment getCurrFragment() {
        return mCurrFragment;
    }

    public AnywhereViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        sInstance = this;
        mViewModel = new ViewModelProvider(this).get(AnywhereViewModel.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initView();

        Observer<List<PageEntity>> observer = new Observer<List<PageEntity>>() {
            @Override
            public void onChanged(List<PageEntity> pageEntities) {
                AnywhereApplication.sRepository.getAllPageEntities().removeObserver(this);

                if (pageEntities.size() == 0 && !isPageInit) {
                    PageEntity pe = PageEntity.Builder();
                    pe.setTitle(GlobalValues.sCategory);
                    pe.setPriority(1);
                    AnywhereApplication.sRepository.insertPage(pe);
                    isPageInit = true;
                }
            }
        };

        AnywhereApplication.sRepository.getAllPageEntities().observe(this, observer);

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE) &&
                SPUtils.getBoolean(this, Const.PREF_FIRST_LAUNCH, true)) {
            mBinding.fab.setVisibility(View.GONE);
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mCurrFragment = MainFragment.newInstance(GlobalValues.sCategory);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(R.id.container, mCurrFragment)
                    .commitNow();
            initFab();
            initObserver();
            getAnywhereIntent(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Settings.setTheme(GlobalValues.sDarkMode);
        ClipboardUtil.getClipBoardText(this, text -> {
            Logger.d("Clipboard: ", text);
            if (text.contains(URLManager.ANYWHERE_SCHEME)) {
                processUri(Uri.parse(text));
                ClipboardUtil.clearClipboard(this);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getAnywhereIntent(intent);
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UiUtils.loadBackgroundPic(this, (ImageView) mBinding.stubBg.getRoot());
        if (mToggle != null) {
            mToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.d("onPrepareOptionsMenu: actionBarType =", GlobalValues.sActionBarType);

        if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_LIGHT)
                || (UiUtils.isDarkMode(this) && GlobalValues.sBackgroundUri.isEmpty())) {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_LIGHT);
        } else {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_DARK);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            Objects.requireNonNull(mBinding.stubBg.getViewStub()).inflate();
            UiUtils.loadBackgroundPic(this, (ImageView) mBinding.stubBg.getRoot());
            UiUtils.setActionBarTransparent(this);
            UiUtils.setAdaptiveActionBarTitleColor(this, getSupportActionBar(), UiUtils.getActionBarTitle());
        }

        if (GlobalValues.sIsMd2Toolbar) {
            int marginHorizontal = (int) getResources().getDimension(R.dimen.toolbar_margin_horizontal);
            int marginVertical = (int) getResources().getDimension(R.dimen.toolbar_margin_vertical);

            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
            newLayoutParams.leftMargin = newLayoutParams.rightMargin = marginHorizontal;
            newLayoutParams.topMargin = newLayoutParams.bottomMargin = marginVertical;
            newLayoutParams.height = UiUtils.d2p(this, 55);
            mBinding.toolbar.setLayoutParams(newLayoutParams);
            mBinding.toolbar.setContentInsetStartWithNavigation(0);
            UiUtils.drawMd2Toolbar(this, mBinding.toolbar, 3);
        }

        if (actionBar != null) {
            if (GlobalValues.sIsPages) {
                mToggle = new ActionBarDrawerToggle(this, mBinding.drawer, mBinding.toolbar, R.string.drawer_open, R.string.drawer_close);
                if (GlobalValues.sActionBarType.equals(Const.ACTION_BAR_TYPE_DARK)) {
                    mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
                } else {
                    mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
                }
                actionBar.setDisplayHomeAsUpEnabled(true);
                mBinding.drawer.addDrawerListener(mToggle);
                mToggle.syncState();

                AnywhereApplication.sRepository
                        .getAllAnywhereEntities()
                        .observe(this, anywhereEntities -> initDrawer(mBinding.drawer));
            } else {
                actionBar.setHomeButtonEnabled(false);
                mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
    }

    private void initDrawer(DrawerLayout drawer) {
        RecyclerView recyclerView = drawer.findViewById(R.id.rv_pages);

        PageListAdapter adapter = new PageListAdapter();
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.iv_entry) {
                MainActivity.getInstance().mBinding.drawer.closeDrawer(GravityCompat.START);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    PageTitleNode node = (PageTitleNode) adapter1.getItem(position);
                    if (node != null) {
                        PageEntity pe = ListUtils.getPageEntityByTitle(node.getTitle());
                        if (pe != null) {
                            if (pe.getType() == AnywhereType.CARD_PAGE) {
                                mCurrFragment = MainFragment.newInstance(pe.getTitle());
                                MainActivity.getInstance().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                                        .replace(R.id.container, mCurrFragment)
                                        .commitNow();
                            } else if (pe.getType() == AnywhereType.WEB_PAGE) {
                                MainActivity.getInstance().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                                        .replace(R.id.container, WebviewFragment.newInstance(pe.getExtra()))
                                        .commitNow();
                            }
                            GlobalValues.setsCategory(pe.getTitle(), position);
                        }
                    }

                }, 300);
            }
        });
        AnywhereApplication.sRepository.getAllPageEntities().observe(this, pageEntities -> {
            if (pageEntities != null) {
                setupDrawerData(adapter, pageEntities);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) Objects.requireNonNull(
                recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        ImageButton ibAdd, ibPageSort, ibDone;
        ibAdd = drawer.findViewById(R.id.ib_add);
        ibPageSort = drawer.findViewById(R.id.ib_sort_page);
        ibDone = drawer.findViewById(R.id.ib_done);

        ibAdd.setOnClickListener(v -> {
            if (IzukoHelper.isHitagi()) {
                DialogManager.showAddPageDialog(MainActivity.this, (dialog, which) -> {
                    if (which == 0) {
                        mViewModel.addPage();
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("text/html");
                            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                            setDocumentResultListener(uri -> mViewModel.addWebPage(uri, intent));
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            ToastUtil.makeText(R.string.toast_no_document_app);
                        }
                    }
                });
            } else {
                mViewModel.addPage();
            }
        });
        ibPageSort.setOnClickListener(v -> {
            ibPageSort.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            for (int i = 0; i < adapter.getData().size(); i++) {
                adapter.collapse(i);
            }
            PageTitleProvider.isEditMode = true;
            ItemTouchCallBack touchCallBack = new ItemTouchCallBack();
            touchCallBack.setOnItemTouchListener(adapter);
            mItemTouchHelper = new ItemTouchHelper(touchCallBack);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
            ibAdd.setVisibility(View.GONE);
            ibPageSort.setVisibility(View.GONE);
            ibDone.setVisibility(View.VISIBLE);
        });
        ibDone.setOnClickListener(v -> {
            ibDone.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            PageTitleProvider.isEditMode = false;
            mItemTouchHelper.attachToRecyclerView(null);
            ibAdd.setVisibility(View.VISIBLE);
            ibPageSort.setVisibility(View.VISIBLE);
            ibDone.setVisibility(View.GONE);

            List<BaseNode> list = adapter.getData();
            HashMap<String, Integer> map = new HashMap<>();
            int i = 1;
            for (BaseNode node : list) {
                if (node instanceof PageTitleNode) {
                    map.put(((PageTitleNode) node).getTitle(), i);
                    i++;
                }
            }
            List<PageEntity> pageEntityList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
            if (pageEntityList != null) {
                for (PageEntity pe : pageEntityList) {
                    pe.setPriority(Objects.requireNonNull(map.get(pe.getTitle())));
                    AnywhereApplication.sRepository.updatePage(pe);
                }
            }
        });
    }

    private void setupDrawerData(PageListAdapter adapter, List<PageEntity> pageEntities) {
        List<BaseNode> list = new ArrayList<>();
        for (PageEntity pe : pageEntities) {
            list.add(mViewModel.getEntity(pe.getTitle()));
        }
        adapter.setNewData(list);
    }

    public void initObserver() {
        mViewModel.getBackground().observe(this, s -> {
            if (!s.isEmpty()) {
                UiUtils.loadBackgroundPic(sInstance, (ImageView) mBinding.stubBg.getRoot());
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
    }

    public void initFab() {
        FabBuilder.build(this, mBinding.fab);
        mBinding.fab.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.fab_url_scheme:
                    mViewModel.setUpUrlScheme("");
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_url_scheme", "click_fab_url_scheme");
                    break;
                case R.id.fab_activity_list:
                    startActivity(new Intent(this, AppListActivity.class));
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_activity_list", "click_fab_activity_list");
                    break;
                case R.id.fab_collector:
                    mViewModel.checkWorkingPermission(this);
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_collector", "click_fab_collector");
                    break;
                case R.id.fab_qr_code_collection:
                    startActivity(new Intent(this, QRCodeCollectionActivity.class));
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_qr_code_collection", "click_fab_qr_code_collection");
                    break;
                case R.id.fab_image:
                    mViewModel.openImageEditor();
                    FirebaseUtil.logEvent(mFirebaseAnalytics, "fab_image", "click_fab_image");
                    break;
                default:
                    return false;
            }
            mBinding.fab.close();
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

            if (uri != null) {
                Logger.d("Received Url =", uri.toString());
                Logger.d("Received path =", uri.getPath());

                processUri(uri);
            }
        } else if (action.equals(Intent.ACTION_SEND)) {
            String sharing = intent.getStringExtra(Intent.EXTRA_TEXT);
            mViewModel.setUpUrlScheme(TextUtils.parseUrlFromSharingText(sharing));
        }
    }

    private void processUri(Uri uri) {
        if (android.text.TextUtils.equals(uri.getHost(), URLManager.URL_HOST)) {
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

                    Editor editor = new AnywhereEditor(this)
                            .item(ae)
                            .isEditorMode(false)
                            .isShortcut(false)
                            .build();
                    editor.show();
                }
            }
        } else if (android.text.TextUtils.equals(uri.getHost(), URLManager.CARD_SHARING_HOST)) {
            if (uri.getPath() != null) {
                String encrypted = uri.getPath().substring(1);
                String decrypted = CipherUtils.decrypt(encrypted);
                AnywhereEntity ae = new Gson().fromJson(decrypted, AnywhereEntity.class);
                Editor editor = new AnywhereEditor(this)
                        .item(ae)
                        .isEditorMode(false)
                        .isShortcut(false)
                        .build();
                editor.show();
            }
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
        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Logger.d("REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
            if (resultCode == RESULT_OK) {
                mViewModel.checkWorkingPermission(this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerVisible(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

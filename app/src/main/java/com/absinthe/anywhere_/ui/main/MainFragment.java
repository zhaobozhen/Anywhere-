package com.absinthe.anywhere_.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.SelectableCardsAdapter;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.Editor;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainFragment extends Fragment implements LifecycleOwner {
    private static final String TAG = MainFragment.class.getSimpleName();
    private Context mContext;
    private int selectedWorkingModeIndex = 0;

    private static AnywhereViewModel mViewModel;
    private SelectableCardsAdapter adapter;
    private ActionBar actionBar;

    static MainFragment newInstance() {
        return new MainFragment();
    }
    public static AnywhereViewModel getViewModelInstance() {
        return mViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        MainActivity.setCurFragment(this);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initObserver();

        if (GlobalValues.sIsFirstLaunch) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.fab)
                    .setPrimaryText("创建你的第一个 Anywhere- 吧！")
                    .setBackgroundColour(getResources().getColor(R.color.colorAccent))
                    .show();
            GlobalValues.setsIsFirstLaunch(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();

        if (bundle != null) {
            String param1 = bundle.getString(Const.INTENT_EXTRA_PARAM_1);
            String param2 = bundle.getString(Const.INTENT_EXTRA_PARAM_2);
            String param3 = bundle.getString(Const.INTENT_EXTRA_PARAM_3);

            Log.d(TAG, "Bundle param1 = " + param1);
            Log.d(TAG, "Bundle param2 = " + param2);
            Log.d(TAG, "Bundle param3 = " + param3);

            if (param1 != null && param2 != null && param3 != null) {
                if (param2.isEmpty() && param3.isEmpty()) {
                    setUpUrlScheme(param1);
                } else {
                    String appName;
                    appName = TextUtils.getAppName(mContext, param1);
                    String timeStamp = System.currentTimeMillis() + "";
                    AnywhereEntity ae = new AnywhereEntity(timeStamp, appName, param1, param2, param3, "", AnywhereType.ACTIVITY, timeStamp);
                    Editor editor = new Editor(MainActivity.getInstance(), Editor.ANYWHERE)
                            .item(ae)
                            .isEditorMode(false)
                            .isShortcut(false)
                            .build();
                    editor.show();
                }
                bundle.clear();
            }
        }

    }

    void checkWorkingPermission() {
        Log.d(TAG, "workingMode = " + GlobalValues.sWorkingMode);
        selectedWorkingModeIndex = 0;
        if (GlobalValues.sWorkingMode != null) {
            if (GlobalValues.sWorkingMode.isEmpty()) {
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle(R.string.settings_working_mode)
                        .setSingleChoiceItems(R.array.list_working_mode, 0, (dialogInterface, i) -> selectedWorkingModeIndex = i)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                            switch (selectedWorkingModeIndex) {
                                case 0:
                                    mViewModel.getWorkingMode().setValue(Const.WORKING_MODE_URL_SCHEME);
                                    break;
                                case 1:
                                    mViewModel.getWorkingMode().setValue(Const.WORKING_MODE_ROOT);
                                    break;
                                case 2:
                                    mViewModel.getWorkingMode().setValue(Const.WORKING_MODE_SHIZUKU);
                                    break;
                                default:
                                    Log.d(TAG, "default");
                            }
                            checkWorkingPermission();
                        })
                        .setNegativeButton(R.string.dialog_delete_negative_button, null)
                        .show();
            }

            switch (GlobalValues.sWorkingMode) {
                case Const.WORKING_MODE_URL_SCHEME:
                    setUpUrlScheme("");
                    break;
                case Const.WORKING_MODE_SHIZUKU:
                    if (!PermissionUtil.checkOverlayPermission(MainActivity.getInstance(), Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtil.checkShizukuOnWorking(mContext) && PermissionUtil.shizukuPermissionCheck(getActivity())) {
                        startCollector();
                    } else {
                        actionBar.setTitle("Nowhere-");
                    }
                    break;
                case Const.WORKING_MODE_ROOT:
                    if (!PermissionUtil.checkOverlayPermission(MainActivity.getInstance(), Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtil.upgradeRootPermission(mContext.getPackageCodePath())) {
                        startCollector();
                    } else {
                        Log.d(TAG, "ROOT permission denied.");
                        ToastUtil.makeText(R.string.toast_root_permission_denied);
                        actionBar.setTitle("Nowhere-");
                    }
                    break;
            }
        }

    }

    private void startCollector() {
        Intent intent = new Intent(mContext, CollectorService.class);
        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
        mContext.startService(intent);
        ToastUtil.makeText(R.string.toast_collector_opened);

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }

    private void setUpUrlScheme(String url) {
        String timeStamp = System.currentTimeMillis() + "";
        AnywhereEntity ae = new AnywhereEntity(timeStamp, getString(R.string.bsd_new_url_scheme_name), url, null, null, "", AnywhereType.URL_SCHEME, timeStamp);
        Editor editor = new Editor(MainActivity.getInstance(), Editor.URL_SCHEME)
                .item(ae)
                .isEditorMode(false)
                .isShortcut(false)
                .build();
        editor.show();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        List<AnywhereEntity> anywhereEntityList = new ArrayList<>();

        adapter = new SelectableCardsAdapter(mContext);
        adapter.setItems(anywhereEntityList);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings) {
            startActivity(new Intent(MainActivity.getInstance(), SettingsActivity.class));
        } else if (item.getItemId() == R.id.toolbar_sort) {
            PopupMenu popup = new PopupMenu(mContext, MainActivity.getInstance().findViewById(R.id.toolbar_sort));
            popup.getMenuInflater()
                    .inflate(R.menu.sort_menu, popup.getMenu());
            if (popup.getMenu() instanceof MenuBuilder) {
                MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
                menuBuilder.setOptionalIconsVisible(true);
            }
            popup.setOnMenuItemClickListener(popupItem -> {
                switch (popupItem.getItemId()) {
                    case R.id.sort:
                        ToastUtil.makeText("Sort");
                        break;
                    default:
                }
                return true;
            });

            popup.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecyclerView(recyclerView);
        setHasOptionsMenu(true);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(clickView -> checkWorkingPermission());
        actionBar = MainActivity.getInstance().getSupportActionBar();
        UiUtils.setActionBarTitle(MainActivity.getInstance(), actionBar);
    }

    private void initObserver() {
        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);
        mViewModel.getWorkingMode().setValue(GlobalValues.sWorkingMode);

        mViewModel.getCommand().observe(this, PermissionUtil::execCmd);
        mViewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> {
            adapter.setItems(anywhereEntities);
            AppUtils.updateWidget(AnywhereApplication.sContext);
        });
        mViewModel.getWorkingMode().observe(this, s -> {
            GlobalValues.setsWorkingMode(s);
            UiUtils.setActionBarTitle(MainActivity.getInstance(), actionBar);
        });

        final Observer<String> backgroundObserver = s -> {
            ImageView ivBackground = MainActivity.getInstance().findViewById(R.id.iv_background);
            if (s.isEmpty()) {
                ivBackground.setBackground(null);
                ivBackground.setVisibility(View.GONE);
                GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_LIGHT);
                UiUtils.resetActionBar(MainActivity.getInstance());
                MainActivity.getInstance().invalidateOptionsMenu();
            } else {
                UiUtils.loadBackgroundPic(mContext, ivBackground);
                UiUtils.setActionBarTransparent(MainActivity.getInstance());
                UiUtils.setAdaptiveActionBarTitleColor(MainActivity.getInstance(), actionBar, UiUtils.getActionBarTitle());
                ivBackground.setVisibility(View.VISIBLE);
            }
            GlobalValues.setsBackgroundUri(s);
        };
        mViewModel.getBackground().observe(this, backgroundObserver);

        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            Log.d(TAG, "backgroundUri = " + GlobalValues.sBackgroundUri);
            mViewModel.getBackground().setValue(GlobalValues.sBackgroundUri);
        }
    }
}

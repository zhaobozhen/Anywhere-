package com.absinthe.anywhere_.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.SelectableCardsAdapter;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ImageUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        boolean isFirstLaunch = SPUtils.getBoolean(mContext, ConstUtil.SP_KEY_FIRST_LAUNCH);
        if (isFirstLaunch) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.fab)
                    .setPrimaryText("创建你的第一个 Anywhere- 吧！")
                    .setBackgroundColour(getResources().getColor(R.color.colorAccent))
                    .show();
            SPUtils.putBoolean(mContext, ConstUtil.SP_KEY_FIRST_LAUNCH, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (GlobalValues.sWorkingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
            Bundle bundle = getArguments();

            if (bundle != null) {
                String shortcutEditUrl = bundle.getString("shortcutEditUrl");
                if (shortcutEditUrl != null) {
                    if (shortcutEditUrl.equals("true")) {
                        EditUtils.editUrlScheme(MainActivity.getInstance());
                    }
                    bundle.clear();
                }
            }
            return;
        }

        Bundle bundle = getArguments();

        if (bundle != null) {
            String packageName = bundle.getString(ConstUtil.BUNDLE_PACKAGE_NAME);
            String className = bundle.getString(ConstUtil.BUNDLE_CLASS_NAME);
            String classNameType = bundle.getInt(ConstUtil.BUNDLE_CLASS_NAME_TYPE) + "";

            String appName;
            if (packageName != null && className != null) {
                appName = TextUtils.getAppName(mContext, packageName);

                Log.d(TAG, "onResume:" + packageName + "," + className);
                EditUtils.editAnywhere(MainActivity.getInstance(), packageName, className, classNameType, appName, "");

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
                                    mViewModel.getWorkingMode().setValue(ConstUtil.WORKING_MODE_URL_SCHEME);
                                    break;
                                case 1:
                                    mViewModel.getWorkingMode().setValue(ConstUtil.WORKING_MODE_ROOT);
                                    break;
                                case 2:
                                    mViewModel.getWorkingMode().setValue(ConstUtil.WORKING_MODE_SHIZUKU);
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
                case ConstUtil.WORKING_MODE_URL_SCHEME:
                    setUpUrlScheme();
                    break;
                case ConstUtil.WORKING_MODE_SHIZUKU:
                    if (!PermissionUtil.checkOverlayPermission(getActivity(), ConstUtil.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtil.checkShizukuOnWorking(mContext) && PermissionUtil.shizukuPermissionCheck(getActivity())) {
                        startCollector();
                    } else {
                        actionBar.setTitle("Nowhere-");
                    }
                    break;
                case ConstUtil.WORKING_MODE_ROOT:
                    if (!PermissionUtil.checkOverlayPermission(getActivity(), ConstUtil.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtil.upgradeRootPermission(mContext.getPackageCodePath())) {
                        startCollector();
                    } else {
                        Log.d(TAG, "ROOT permission denied.");
                        Toast.makeText(mContext, getString(R.string.toast_root_permission_denied), Toast.LENGTH_SHORT).show();
                        actionBar.setTitle("Nowhere-");
                    }
                    break;
            }
        }

    }

    private void startCollector() {
        Intent intent = new Intent(mContext, CollectorService.class);
        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
        Toast.makeText(getContext(), R.string.toast_collector_opened, Toast.LENGTH_SHORT).show();

        mContext.startService(intent);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }

    private void setUpUrlScheme() {
        EditUtils.editUrlScheme(MainActivity.getInstance());
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
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecyclerView(recyclerView);
        setHasOptionsMenu(true);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(clickView -> checkWorkingPermission());
        actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        ImageUtils.setActionBarTitle(getActivity(), actionBar);

        View placeholder = view.findViewById(R.id.placeholder);
        if (SPUtils.getBoolean(mContext, ConstUtil.SP_KEY_FIRST_LAUNCH)) {
            placeholder.setVisibility(View.VISIBLE);
        }
    }

    private void initObserver() {
        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);
        mViewModel.getWorkingMode().setValue(GlobalValues.sWorkingMode);

        final Observer<String> commandObserver = s -> {
            switch (GlobalValues.sWorkingMode) {
                case ConstUtil.WORKING_MODE_SHIZUKU:
                    if (PermissionUtil.shizukuPermissionCheck(getActivity())) {
                        PermissionUtil.execShizukuCmd(s);
                    }
                    break;
                case ConstUtil.WORKING_MODE_ROOT:
                    if (PermissionUtil.upgradeRootPermission(mContext.getPackageCodePath())) {
                        PermissionUtil.execRootCmd(s);
                    }
                    break;
                case ConstUtil.WORKING_MODE_URL_SCHEME:
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse(s));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Log.d(TAG, "WORKING_MODE_URL_SCHEME:Exception:" + e.getMessage());
                        Toast.makeText(mContext, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        };
        mViewModel.getCommand().observe(this, commandObserver);
        mViewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> adapter.setItems(anywhereEntities));
        mViewModel.getWorkingMode().observe(this, s -> {
            GlobalValues.setsWorkingMode(s);
            SPUtils.putString(mContext, ConstUtil.SP_KEY_WORKING_MODE, s);
            ImageUtils.setActionBarTitle(getActivity(), actionBar);
        });

        final Observer<String> backgroundObserver = s -> {
            ImageView ivBackground = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_background);
            if (s.isEmpty()) {
                ivBackground.setBackground(null);
                ivBackground.setVisibility(View.GONE);
                SPUtils.putString(mContext, ConstUtil.SP_KEY_ACTION_BAR_TYPE, ConstUtil.ACTION_BAR_TYPE_LIGHT);
                ImageUtils.resetActionBar(getActivity());
                getActivity().invalidateOptionsMenu();
            } else {
                ImageUtils.loadBackgroundPic(mContext, ivBackground);
                ImageUtils.setActionBarTransparent(getActivity());
                ImageUtils.setAdaptiveActionBarTitleColor(getActivity(), actionBar, ImageUtils.getActionBarTitle());
                ivBackground.setVisibility(View.VISIBLE);
            }
            SPUtils.putString(mContext, ConstUtil.SP_KEY_CHANGE_BACKGROUND, s);
        };
        mViewModel.getBackground().observe(this, backgroundObserver);

        String backgroundUri = SPUtils.getString(mContext, ConstUtil.SP_KEY_CHANGE_BACKGROUND);

        if (!backgroundUri.isEmpty()) {
            Log.d(TAG, "backgroundUri = " + backgroundUri);
            mViewModel.getBackground().setValue(backgroundUri);
        }
    }
}

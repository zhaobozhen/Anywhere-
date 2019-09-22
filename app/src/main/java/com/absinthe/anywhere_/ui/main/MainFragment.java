package com.absinthe.anywhere_.ui.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuService;

public class MainFragment extends Fragment implements LifecycleOwner {
    private static final String TAG = "MainFragment";
    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISSION_V3 = 2;
    private static final int REQUEST_CODE_AUTHORIZATION_V3 = 3;
    private Context mContext;

    private static AnywhereViewModel mViewModel;
    private FloatingActionButton fab;
    private BottomSheetDialog bottomSheetDialog;
    private SelectableCardsAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }
    public static AnywhereViewModel getViewModelInstance() {
        return mViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecyclerView(recyclerView);
        setHasOptionsMenu(true);

        fab = view.findViewById(R.id.fab);


        bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(mContext));
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_content);
        bottomSheetDialog.setDismissWithAnimation(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);

        final Observer<String> commandObserver = s -> {
            if (shizukuPermissionCheck()) {
                PermissionUtil.execShizukuCmd(s);
            }
        };
        mViewModel.getCommand().observe(this, commandObserver);
        mViewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> adapter.setItems(anywhereEntities));

        fab.setOnClickListener(view -> {
            if (checkOverlayPermission()) {
                startCollector();
            }
        });

        checkShizukuOnWorking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String packageName = bundle.getString("packageName");
            String className = bundle.getString("className");
            int classNameType = bundle.getInt("classNameType");

            String appName;
            if (packageName != null && className != null) {
                appName = TextUtils.getAppName(mContext, packageName);

                Log.d(TAG, "onResume:" + packageName + "," + className);
                editNewAnywhere(packageName, className, classNameType, appName);

                bundle.clear();
            }
        }

    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                startActivityForResult(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName())),
                        REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION
                );
                Toast.makeText(mContext, R.string.toast_permission_overlap, Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void startCollector() {
        if (checkShizukuOnWorking() && shizukuPermissionCheck()) {
            Intent intent = new Intent(mContext, CollectorService.class);
            intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
            Toast.makeText(getContext(), R.string.toast_collector_opened, Toast.LENGTH_SHORT).show();

            mContext.startService(intent);
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION && Settings.canDrawOverlays(mContext)) {
            startCollector();
        }
    }

    private boolean checkShizukuOnWorking() {
        // Shizuku v3 service will send binder via Content Provider to this process when this activity comes foreground.

        // Wait a few seconds here for binder

        if (!ShizukuService.pingBinder()) {
            if (AnywhereApplication.isShizukuV3Failed()) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                Toast.makeText(mContext, "provider started with no binder included.", Toast.LENGTH_SHORT).show();
            }

            // Shizuku v3 may not running, notify user
            Toast.makeText(mContext, "Shizuku v3 may not running.", Toast.LENGTH_SHORT).show();
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true;
        }
        return false;
    }

    private boolean shizukuPermissionCheck() {
        if (!ShizukuClientHelper.isPreM()) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (ActivityCompat.checkSelfPermission(mContext, ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtil.isMIUI()) {
                    showPermissionDialog();
                } else {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{ShizukuApiConstants.PERMISSION}, REQUEST_CODE_PERMISSION_V3);
                }
                    return false;
            } else {
                return true;
            }
        } else if (!AnywhereApplication.isShizukuV3TokenValid()){
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelper.createPre23AuthorizationIntent(mContext);
            if (intent != null) {
                try {
                    startActivityForResult(intent, REQUEST_CODE_AUTHORIZATION_V3);
                    return true;
                } catch (Throwable tr) {
                    // should never happened
                    return false;
                }
            } else {
                // activity not found
                Log.d(TAG, "activity not found.");
                Toast.makeText(mContext, "activity not found.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
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
        inflater.inflate(R.menu.bottom_bar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void editNewAnywhere(String packageName, String className, int classNameType, String appName) {
        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietPackageName = bottomSheetDialog.findViewById(R.id.tiet_package_name);
        TextInputEditText tietClassName = bottomSheetDialog.findViewById(R.id.tiet_class_name);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);


        if (tietAppName != null) {
            tietAppName.setText(appName);
        }

        if (tietPackageName != null) {
            tietPackageName.setText(packageName);
        }

        if (tietClassName != null) {
            tietClassName.setText(className);
        }

        if (tietDescription != null) {
            tietDescription.setText(null);
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null) {
                    String pName = tietPackageName.getText() == null ? packageName : tietPackageName.getText().toString();
                    String cName = tietClassName.getText() == null ? className : tietClassName.getText().toString();
                    String aName = tietAppName.getText() == null ? appName : tietAppName.getText().toString();
                    String description = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                    Log.d(TAG, "description == " + description);

                    mViewModel.insert(new AnywhereEntity(pName, cName, classNameType, aName, description));
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(mContext, "error data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        bottomSheetDialog.show();
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Not install Shizuku.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> { })
                .show();
    }
}

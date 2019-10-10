package com.absinthe.anywhere_.ui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.AnimationUtil;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.viewmodel.InitializeViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class InitializeFragment extends Fragment implements MaterialButtonToggleGroup.OnButtonCheckedListener, LifecycleOwner {
    private static final String TAG = InitializeFragment.class.getSimpleName();

    private Context mContext;
    private InitializeViewModel mViewModel;
    private MaterialCardView cvRoot, cvShizuku, cvOverlay, cvPopup;
    private Button btnRoot, btnShizukuCheck, btnShizuku, btnOverlay, btnPopup;

    private String workingMode;

    static InitializeFragment newInstance() {
        return new InitializeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.initialize_fragment, container, false);
        initView(view);
        MainActivity.setCurFragment(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = MainActivity.getInstance();
        mViewModel = ViewModelProviders.of(this).get(InitializeViewModel.class);
        workingMode = ConstUtil.WORKING_MODE_URL_SCHEME;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.OVERLAY_PERM);
        }

        initObserver();
    }

    private void initView(View view) {
        cvRoot = view.findViewById(R.id.cv_acquire_root_permission);
        cvShizuku = view.findViewById(R.id.cv_acquire_shizuku_permission);
        cvOverlay = view.findViewById(R.id.cv_acquire_overlay_permission);
        cvPopup = view.findViewById(R.id.cv_acquire_popup_permission);

        cvRoot.setVisibility(View.GONE);
        cvShizuku.setVisibility(View.GONE);
        cvOverlay.setVisibility(View.GONE);
        cvPopup.setVisibility(View.GONE);

        btnRoot = cvRoot.findViewById(R.id.btn_acquire_root_permission);
        btnShizuku = cvShizuku.findViewById(R.id.btn_acquire_permission);
        btnShizukuCheck = cvShizuku.findViewById(R.id.btn_check_shizuku_state);
        btnOverlay = cvOverlay.findViewById(R.id.btn_acquire_overlay_permission);
        btnPopup = cvPopup.findViewById(R.id.btn_acquire_popup_permission);
        btnShizuku.setEnabled(false);

        setOnClickListener();

        setHasOptionsMenu(true);

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggle_group);
        toggleGroup.addOnButtonCheckedListener(this);
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        Log.d(TAG, "onButtonChecked");

        switch (checkedId) {
            case R.id.btn_url_scheme:
                if (isChecked) {
                    hidePermissionCard();
                    workingMode = ConstUtil.WORKING_MODE_URL_SCHEME;
                }
                break;
            case R.id.btn_root:
                if (isChecked) {
                    hidePermissionCard();
                    showRootCard();
                    workingMode = ConstUtil.WORKING_MODE_ROOT;
                }
                break;
            case R.id.btn_shizuku:
                if (isChecked) {
                    hidePermissionCard();
                    showShizukuCard();
                    workingMode = ConstUtil.WORKING_MODE_SHIZUKU;
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.initialize_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_initialize_done) {
            SPUtils.putString(mContext, ConstUtil.SP_KEY_WORKING_MODE, workingMode);
            AnywhereApplication.workingMode = workingMode;

            boolean flag = false;
            int allPerm = Objects.requireNonNull(mViewModel.getAllPerm().getValue());
            switch (workingMode) {
                case ConstUtil.WORKING_MODE_URL_SCHEME:
                    flag = true;
                    break;
                case ConstUtil.WORKING_MODE_ROOT:
                    if (allPerm == (InitializeViewModel.ROOT_PERM | InitializeViewModel.OVERLAY_PERM)) {
                        flag = true;
                    }
                    break;
                case ConstUtil.WORKING_MODE_SHIZUKU:
                    if (allPerm == (InitializeViewModel.SHIZUKU_GROUP_PERM | InitializeViewModel.OVERLAY_PERM)) {
                        flag = true;
                    }
                    break;
                default:
            }

            if (flag) {
                MainActivity.getInstance()
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainFragment.newInstance())
                        .commitNow();
            } else {
                new MaterialAlertDialogBuilder(mContext)
                        .setMessage(R.string.dialog_message_perm_not_ever)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) ->
                                MainActivity.getInstance()
                                .getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, MainFragment.newInstance())
                                .commitNow())
                        .setNegativeButton(R.string.dialog_delete_negative_button, null)
                        .show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnClickListener() {
        btnRoot.setOnClickListener(view -> {
            boolean result = PermissionUtil.upgradeRootPermission(mContext.getPackageCodePath());
            mViewModel.getIsRoot().setValue(result);
        });

        btnShizukuCheck.setOnClickListener(view -> {
            boolean result = PermissionUtil.checkShizukuOnWorking(mContext);
            mViewModel.getIsShizukuCheck().setValue(result);
        });

        btnShizuku.setOnClickListener(view -> {
            boolean result = PermissionUtil.shizukuPermissionCheck((Activity) mContext);
            mViewModel.getIsShizuku().setValue(result);
        });

        btnOverlay.setOnClickListener(view -> {
            boolean result = PermissionUtil.checkOverlayPermission((Activity) mContext, ConstUtil.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION);
            mViewModel.getIsOverlay().setValue(result);
        });

        btnPopup.setOnClickListener(view -> {
            if (PermissionUtil.isMIUI()) {
                PermissionUtil.goToMIUIPermissionManager(mContext);
            }
        });
    }

    private void initObserver() {
        mViewModel.getIsRoot().observe(this, aBoolean -> {
            if (aBoolean) {
                btnRoot.setText(R.string.btn_acquired);
                btnRoot.setEnabled(false);
                cvRoot.findViewById(R.id.done).setVisibility(View.VISIBLE);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.ROOT_PERM);
                Log.d(TAG, "allPerm = " + mViewModel.getAllPerm().getValue());

            } else {
                Log.d(TAG, "ROOT permission denied.");
                Toast.makeText(mContext, getString(R.string.toast_root_permission_denied), Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getIsShizukuCheck().observe(this, aBoolean -> {
            if (aBoolean) {
                btnShizukuCheck.setText(R.string.btn_checked);
                btnShizukuCheck.setEnabled(false);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.SHIZUKU_CHECK_PERM);
                btnShizuku.setEnabled(true);
            }
            if ((Objects.requireNonNull(mViewModel.getAllPerm().getValue()) & InitializeViewModel.SHIZUKU_GROUP_PERM) == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                cvShizuku.findViewById(R.id.done).setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getIsShizuku().observe(this, aBoolean -> {
            if (aBoolean) {
                btnShizuku.setText(R.string.btn_acquired);
                btnShizuku.setEnabled(false);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.SHIZUKU_PERM);
            }
            if ((Objects.requireNonNull(mViewModel.getAllPerm().getValue()) & InitializeViewModel.SHIZUKU_GROUP_PERM) == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                cvShizuku.findViewById(R.id.done).setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getIsOverlay().observe(this, aBoolean -> {
            if (aBoolean) {
                btnOverlay.setText(R.string.btn_acquired);
                btnOverlay.setEnabled(false);
                cvOverlay.findViewById(R.id.done).setVisibility(View.VISIBLE);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.OVERLAY_PERM);
                Log.d(TAG, "allPerm = " + mViewModel.getAllPerm().getValue());
            }
        });

    }

    private void hidePermissionCard() {
        Log.d(TAG, "hidePermissionCard");

        if (cvRoot.getVisibility() == View.VISIBLE) {
            AnimationUtil.showAndHiddenAnimation(cvRoot, AnimationUtil.AnimationState.STATE_GONE, AnimationUtil.SHORT);
        }

        if (cvShizuku.getVisibility() == View.VISIBLE) {
            AnimationUtil.showAndHiddenAnimation(cvShizuku, AnimationUtil.AnimationState.STATE_GONE, AnimationUtil.SHORT);
        }

        if (cvOverlay.getVisibility() == View.VISIBLE) {
            AnimationUtil.showAndHiddenAnimation(cvOverlay, AnimationUtil.AnimationState.STATE_GONE, AnimationUtil.SHORT);
        }

        if (cvPopup.getVisibility() == View.VISIBLE) {
            AnimationUtil.showAndHiddenAnimation(cvPopup, AnimationUtil.AnimationState.STATE_GONE, AnimationUtil.SHORT);
        }
    }

    private void showRootCard() {
        if (cvRoot.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvRoot, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && cvOverlay.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvOverlay, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }

        if (PermissionUtil.isMIUI() && cvPopup.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvPopup, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }
    }

    private void showShizukuCard() {
        if (cvShizuku.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvShizuku, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && cvOverlay.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvOverlay, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }

        if (PermissionUtil.isMIUI() && cvPopup.getVisibility() == View.GONE) {
            AnimationUtil.showAndHiddenAnimation(cvPopup, AnimationUtil.AnimationState.STATE_SHOW, AnimationUtil.SHORT);
        }
    }

}

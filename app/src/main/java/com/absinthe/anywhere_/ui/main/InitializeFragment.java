package com.absinthe.anywhere_.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.CardAcquireOverlayPermissionBinding;
import com.absinthe.anywhere_.databinding.CardAcquirePopupPermissionBinding;
import com.absinthe.anywhere_.databinding.CardAcquireRootPermissionBinding;
import com.absinthe.anywhere_.databinding.CardAcquireShizukuPermissionBinding;
import com.absinthe.anywhere_.databinding.FragmentInitializeBinding;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.ShizukuHelper;
import com.absinthe.anywhere_.viewmodel.InitializeViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Objects;

import moe.shizuku.api.ShizukuApiConstants;
import timber.log.Timber;

public class InitializeFragment extends Fragment implements MaterialButtonToggleGroup.OnButtonCheckedListener, LifecycleOwner {

    private static final int CARD_ROOT = 1;
    private static final int CARD_SHIZUKU = 2;
    private static final int CARD_OVERLAY = 3;
    private static final int CARD_POPUP = 4;

    private static InitializeViewModel mViewModel;

    private Context mContext;
    private FragmentInitializeBinding mBinding;
    private CardAcquireRootPermissionBinding rootBinding;
    private CardAcquireShizukuPermissionBinding shizukuBinding;
    private CardAcquireOverlayPermissionBinding overlayBinding;
    private CardAcquirePopupPermissionBinding popupBinding;

    private boolean bRoot, bShizuku, bOverlay, bPopup;
    private String mWorkingMode;

    static InitializeFragment newInstance() {
        return new InitializeFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInitializeBinding.inflate(inflater, container, false);
        rootBinding = CardAcquireRootPermissionBinding.inflate(inflater, container, false);
        shizukuBinding = CardAcquireShizukuPermissionBinding.inflate(inflater, container, false);
        overlayBinding = CardAcquireOverlayPermissionBinding.inflate(inflater, container, false);
        popupBinding = CardAcquirePopupPermissionBinding.inflate(inflater, container, false);
        initView();

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(InitializeViewModel.class);
        mWorkingMode = Const.WORKING_MODE_URL_SCHEME;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.OVERLAY_PERM);
        }

        initObserver();
    }

    private void initView() {
        bRoot = bShizuku = bOverlay = bPopup = false;

        setHasOptionsMenu(true);
        mBinding.selectWorkingMode.toggleGroup.addOnButtonCheckedListener(this);
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        Timber.d("onButtonChecked");

        switch (checkedId) {
            case R.id.btn_url_scheme:
                if (isChecked) {
                    actCards(CARD_ROOT, false);
                    actCards(CARD_SHIZUKU, false);
                    actCards(CARD_OVERLAY, false);
                    actCards(CARD_POPUP, false);
                    mWorkingMode = Const.WORKING_MODE_URL_SCHEME;
                }
                break;
            case R.id.btn_root:
                if (isChecked) {
                    actCards(CARD_SHIZUKU, false);
                    actCards(CARD_ROOT, true);
                    actCards(CARD_OVERLAY, true);
                    actCards(CARD_POPUP, true);
                    mWorkingMode = Const.WORKING_MODE_ROOT;
                }
                break;
            case R.id.btn_shizuku:
                if (isChecked) {
                    actCards(CARD_ROOT, false);
                    actCards(CARD_SHIZUKU, true);
                    actCards(CARD_OVERLAY, true);
                    actCards(CARD_POPUP, true);
                    mWorkingMode = Const.WORKING_MODE_SHIZUKU;
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
            GlobalValues.setsWorkingMode(mWorkingMode);

            boolean flag = false;
            int allPerm = Objects.requireNonNull(mViewModel.getAllPerm().getValue());
            switch (mWorkingMode) {
                case Const.WORKING_MODE_URL_SCHEME:
                    flag = true;
                    break;
                case Const.WORKING_MODE_ROOT:
                    if (allPerm == (InitializeViewModel.ROOT_PERM | InitializeViewModel.OVERLAY_PERM)) {
                        flag = true;
                    }
                    break;
                case Const.WORKING_MODE_SHIZUKU:
                    if (allPerm == (InitializeViewModel.SHIZUKU_GROUP_PERM | InitializeViewModel.OVERLAY_PERM)) {
                        flag = true;
                    }
                    break;
                default:
            }

            if (flag) {
                enterMainFragment();
            } else {
                DialogManager.showHasNotGrantPermYetDialog(mContext, (dialog, which) -> enterMainFragment());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void enterMainFragment() {
        MainFragment fragment = MainFragment.newInstance(GlobalValues.sCategory);
        MainActivity.getInstance().getViewModel().getFragment().setValue(fragment);
        MainActivity.getInstance().initFab();
        MainActivity.getInstance().initObserver();
    }

    private void initObserver() {
        mViewModel.isRoot().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                rootBinding.btnAcquireRootPermission.setText(R.string.btn_acquired);
                rootBinding.btnAcquireRootPermission.setEnabled(false);
                rootBinding.done.setVisibility(View.VISIBLE);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.ROOT_PERM);
                Timber.d("allPerm = %s", mViewModel.getAllPerm().getValue());
            } else {
                Timber.d("ROOT permission denied.");
                ToastUtil.makeText(R.string.toast_root_permission_denied);
            }
        });

        mViewModel.isShizukuCheck().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                shizukuBinding.btnCheckShizukuState.setText(R.string.btn_checked);
                shizukuBinding.btnCheckShizukuState.setEnabled(false);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.SHIZUKU_CHECK_PERM);
                shizukuBinding.btnAcquirePermission.setEnabled(true);
            }
            if ((Objects.requireNonNull(mViewModel.getAllPerm().getValue()) & InitializeViewModel.SHIZUKU_GROUP_PERM) == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                shizukuBinding.done.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.isShizuku().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                shizukuBinding.btnAcquirePermission.setText(R.string.btn_acquired);
                shizukuBinding.btnAcquirePermission.setEnabled(false);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.SHIZUKU_PERM);
            }
            if ((Objects.requireNonNull(mViewModel.getAllPerm().getValue()) & InitializeViewModel.SHIZUKU_GROUP_PERM) == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                shizukuBinding.done.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.isOverlay().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                overlayBinding.btnAcquireOverlayPermission.setText(R.string.btn_acquired);
                overlayBinding.btnAcquireOverlayPermission.setEnabled(false);
                overlayBinding.done.setVisibility(View.VISIBLE);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.OVERLAY_PERM);
                Timber.d("allPerm = %s", mViewModel.getAllPerm().getValue());
            }
        });

    }

    private void actCards(int card, boolean isAdd) {

        switch (card) {
            case CARD_ROOT:
                rootBinding.btnAcquireRootPermission.setOnClickListener(view -> {
                    boolean result = PermissionUtils.upgradeRootPermission(mContext.getPackageCodePath());
                    mViewModel.isRoot().setValue(result);
                });
                if (isAdd) {
                    if (!bRoot) {
                        mBinding.container.addView(rootBinding.getRoot(), 1);
                        bRoot = true;
                    }
                } else {
                    mBinding.container.removeView(rootBinding.getRoot());
                    bRoot = false;
                }
                break;
            case CARD_SHIZUKU:
                shizukuBinding.btnAcquirePermission.setEnabled(false);
                shizukuBinding.btnCheckShizukuState.setOnClickListener(view -> {
                    boolean result = ShizukuHelper.checkShizukuOnWorking(mContext);
                    mViewModel.isShizukuCheck().setValue(result);
                });

                shizukuBinding.btnAcquirePermission.setOnClickListener(view -> {
                    boolean result = ShizukuHelper.isGrantShizukuPermission();
                    mViewModel.isShizuku().setValue(result);
                    if (!result) {
                        ShizukuHelper.requestShizukuPermission();
                    }
                });
                if (isAdd) {
                    if (!bShizuku) {
                        mBinding.container.addView(shizukuBinding.getRoot(), 1);
                        bShizuku = true;
                    }
                } else {
                    mBinding.container.removeView(shizukuBinding.getRoot());
                    bShizuku = false;
                }
                break;
            case CARD_OVERLAY:
                overlayBinding.btnAcquireOverlayPermission.setOnClickListener(view -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        mViewModel.isOverlay().setValue(true);
                    } else {
                        boolean isGrant = com.blankj.utilcode.util.PermissionUtils.isGrantedDrawOverlays();
                        mViewModel.isOverlay().setValue(isGrant);
                        if (!isGrant) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                ToastUtil.makeText(R.string.toast_overlay_choose_anywhere);
                            }
                            com.blankj.utilcode.util.PermissionUtils.requestDrawOverlays(new com.blankj.utilcode.util.PermissionUtils.SimpleCallback() {
                                @Override
                                public void onGranted() {
                                    mViewModel.isOverlay().setValue(true);
                                }

                                @Override
                                public void onDenied() {
                                    mViewModel.isOverlay().setValue(false);
                                }
                            });
                        }
                    }
                });
                if (isAdd) {
                    if (!bOverlay) {
                        mBinding.container.addView(overlayBinding.getRoot(), -1);
                        bOverlay = true;
                    }
                } else {
                    mBinding.container.removeView(overlayBinding.getRoot());
                    bOverlay = false;
                }
                break;
            case CARD_POPUP:
            default:
                popupBinding.btnAcquirePopupPermission.setOnClickListener(view -> {
                    if (PermissionUtils.isMIUI()) {
                        PermissionUtils.goToMIUIPermissionManager(mContext);
                    }
                });
                if (isAdd) {
                    if (!bPopup) {
                        mBinding.container.addView(popupBinding.getRoot(), -1);
                        bPopup = true;
                    }
                } else {
                    mBinding.container.removeView(popupBinding.getRoot());
                    bPopup = false;
                }
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mContext)) {
                    mViewModel.isOverlay().setValue(Boolean.TRUE);
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (ActivityCompat.checkSelfPermission(mContext, ShizukuApiConstants.PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    mViewModel.isShizuku().setValue(Boolean.TRUE);
                }
            }, 3000);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(mContext, ShizukuApiConstants.PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                mViewModel.isShizuku().setValue(Boolean.TRUE);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

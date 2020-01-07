package com.absinthe.anywhere_.ui.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.viewmodel.InitializeViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class InitializeFragment extends Fragment implements MaterialButtonToggleGroup.OnButtonCheckedListener, LifecycleOwner {
    private static final int CARD_ROOT = 1;
    private static final int CARD_SHIZUKU = 2;
    private static final int CARD_OVERLAY = 3;
    private static final int CARD_POPUP = 4;

    private static InitializeViewModel mViewModel;

    private Context mContext;
    private ViewGroup mContainerView, vgRoot, vgShizuku, vgOverlay, vgPopup;
    private MaterialCardView cvRoot, cvShizuku, cvOverlay, cvPopup;
    private Button btnRoot, btnShizukuCheck, btnShizuku, btnOverlay, btnPopup;
    private boolean bRoot, bShizuku, bOverlay, bPopup;

    private String workingMode;

    static InitializeFragment newInstance() {
        return new InitializeFragment();
    }

    static InitializeViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initialize, container, false);
        initView(view);
        MainActivity.setCurFragment(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(InitializeViewModel.class);
        workingMode = Const.WORKING_MODE_URL_SCHEME;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.OVERLAY_PERM);
        }

        initObserver();
    }

    private void initView(View view) {
        mContainerView = view.findViewById(R.id.container);
        bRoot = bShizuku = bOverlay = bPopup = false;

        setHasOptionsMenu(true);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggle_group);
        toggleGroup.addOnButtonCheckedListener(this);
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        Logger.d("onButtonChecked");

        switch (checkedId) {
            case R.id.btn_url_scheme:
                if (isChecked) {
                    actCards(CARD_ROOT, false);
                    actCards(CARD_SHIZUKU, false);
                    actCards(CARD_OVERLAY, false);
                    actCards(CARD_POPUP, false);
                    workingMode = Const.WORKING_MODE_URL_SCHEME;
                }
                break;
            case R.id.btn_root:
                if (isChecked) {
                    actCards(CARD_SHIZUKU, false);
                    actCards(CARD_ROOT, true);
                    actCards(CARD_OVERLAY, true);
                    actCards(CARD_POPUP, true);
                    workingMode = Const.WORKING_MODE_ROOT;
                }
                break;
            case R.id.btn_shizuku:
                if (isChecked) {
                    actCards(CARD_ROOT, false);
                    actCards(CARD_SHIZUKU, true);
                    actCards(CARD_OVERLAY, true);
                    actCards(CARD_POPUP, true);
                    workingMode = Const.WORKING_MODE_SHIZUKU;
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.initialize_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_initialize_done) {
            GlobalValues.setsWorkingMode(workingMode);

            boolean flag = false;
            int allPerm = Objects.requireNonNull(mViewModel.getAllPerm().getValue());
            switch (workingMode) {
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
                new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                        .setMessage(R.string.dialog_message_perm_not_ever)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> enterMainFragment())
                        .setNegativeButton(R.string.dialog_delete_negative_button, null)
                        .show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void enterMainFragment() {
        MainActivity.getInstance().mViewFlipper.setDisplayedChild(0);
        MainActivity.getInstance().mFab.setVisibility(View.VISIBLE);
        MainActivity.getInstance().initFab();
    }

    private void initObserver() {
        mViewModel.getIsRoot().observe(this, aBoolean -> {
            if (aBoolean) {
                btnRoot.setText(R.string.btn_acquired);
                btnRoot.setEnabled(false);
                cvRoot.findViewById(R.id.done).setVisibility(View.VISIBLE);
                mViewModel.getAllPerm().setValue(Objects.requireNonNull(mViewModel.getAllPerm().getValue()) | InitializeViewModel.ROOT_PERM);
                Logger.d("allPerm = " + mViewModel.getAllPerm().getValue());

            } else {
                Logger.d("ROOT permission denied.");
                ToastUtil.makeText(R.string.toast_root_permission_denied);
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
                Logger.d("allPerm = " + mViewModel.getAllPerm().getValue());
            }
        });

    }

    private void actCards(int card, boolean isAdd) {

        switch (card) {
            case CARD_ROOT:
                if (vgRoot == null) {
                    vgRoot = (ViewGroup) LayoutInflater.from(mContext).inflate(
                            R.layout.card_acquire_root_permission, mContainerView, false);
                }
                cvRoot = vgRoot.findViewById(R.id.cv_acquire_root_permission);
                btnRoot = cvRoot.findViewById(R.id.btn_acquire_root_permission);
                btnRoot.setOnClickListener(view -> {
                    boolean result = PermissionUtils.upgradeRootPermission(mContext.getPackageCodePath());
                    mViewModel.getIsRoot().setValue(result);
                });
                if (isAdd) {
                    if (!bRoot) {
                        mContainerView.addView(vgRoot, 1);
                        bRoot = true;
                    }
                } else {
                    mContainerView.removeView(vgRoot);
                    bRoot = false;
                }
                break;
            case CARD_SHIZUKU:
                if (vgShizuku == null) {
                    vgShizuku = (ViewGroup) LayoutInflater.from(mContext).inflate(
                            R.layout.card_acquire_shizuku_permission, mContainerView, false);
                }
                cvShizuku = vgShizuku.findViewById(R.id.cv_acquire_shizuku_permission);
                btnShizuku = cvShizuku.findViewById(R.id.btn_acquire_permission);
                btnShizukuCheck = cvShizuku.findViewById(R.id.btn_check_shizuku_state);
                btnShizuku.setEnabled(false);
                btnShizukuCheck.setOnClickListener(view -> {
                    boolean result = PermissionUtils.checkShizukuOnWorking(mContext);
                    mViewModel.getIsShizukuCheck().setValue(result);
                });

                btnShizuku.setOnClickListener(view -> {
                    boolean result = PermissionUtils.shizukuPermissionCheck((AppCompatActivity) mContext);
                    mViewModel.getIsShizuku().setValue(result);
                });
                if (isAdd) {
                    if (!bShizuku) {
                        mContainerView.addView(vgShizuku, 1);
                        bShizuku = true;
                    }
                } else {
                    mContainerView.removeView(vgShizuku);
                    bShizuku = false;
                }
                break;
            case CARD_OVERLAY:
                if (vgOverlay == null) {
                    vgOverlay = (ViewGroup) LayoutInflater.from(mContext).inflate(
                            R.layout.card_acquire_overlay_permission, mContainerView, false);
                }
                cvOverlay = vgOverlay.findViewById(R.id.cv_acquire_overlay_permission);
                btnOverlay = cvOverlay.findViewById(R.id.btn_acquire_overlay_permission);
                btnOverlay.setOnClickListener(view -> {
                    boolean result = PermissionUtils.checkOverlayPermission(MainActivity.getInstance(), Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION);
                    mViewModel.getIsOverlay().setValue(result);
                });
                if (isAdd) {
                    if (!bOverlay) {
                        mContainerView.addView(vgOverlay, -1);
                        bOverlay = true;
                    }
                } else {
                    mContainerView.removeView(vgOverlay);
                    bOverlay = false;
                }
                break;
            case CARD_POPUP:
            default:
                if (vgPopup == null) {
                    vgPopup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                            R.layout.card_acquire_popup_permission, mContainerView, false);
                }
                cvPopup = vgPopup.findViewById(R.id.cv_acquire_popup_permission);
                btnPopup = cvPopup.findViewById(R.id.btn_acquire_popup_permission);
                btnPopup.setOnClickListener(view -> {
                    if (PermissionUtils.isMIUI()) {
                        PermissionUtils.goToMIUIPermissionManager(mContext);
                    }
                });
                if (isAdd) {
                    if (!bPopup) {
                        mContainerView.addView(vgPopup, -1);
                        bPopup = true;
                    }
                } else {
                    mContainerView.removeView(vgPopup);
                    bPopup = false;
                }
                break;
        }

    }
}

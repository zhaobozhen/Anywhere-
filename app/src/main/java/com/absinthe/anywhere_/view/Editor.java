package com.absinthe.anywhere_.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.services.OverlayService;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import jonathanfinerty.once.Once;

public abstract class Editor<T extends Editor<?>> {
    public static final int ANYWHERE = 1;
    public static final int URL_SCHEME = 2;
    public static final int QR_CODE = 3;
    public static final int IMAGE = 4;

    protected Context mContext;
    private OnEditorListener mListener;

    AnywhereBottomSheetDialog mBottomSheetDialog;
    AnywhereEntity mItem;
    TextInputLayout tilAppName;
    TextInputEditText tietAppName, tietDescription;

    private int mEditorType;
    private boolean isExported;
    private boolean isShortcut;
    boolean isEditMode;

    Editor(Context context, int editorType) {
        mContext = context;
        mBottomSheetDialog = new AnywhereBottomSheetDialog(context);
        mEditorType = editorType;
        isShortcut = false;
        isEditMode = false;
        isExported = false;

        setBottomSheetDialog();
    }

    public T build() {
        initView();
        setDoneButton();
        setRunButton();
        setMoreButton();
        setOverlayButton();

        return getThis();
    }

    public T isShortcut(boolean flag) {
        isShortcut = flag;
        return getThis();
    }

    public T isEditorMode(boolean flag) {
        isEditMode = flag;
        return getThis();
    }

    public T isExported(boolean flag) {
        isExported = flag;
        return getThis();
    }

    public T item(AnywhereEntity item) {
        mItem = item;
        return getThis();
    }

    public T setOnEditorListener(OnEditorListener listener) {
        mListener = listener;
        return getThis();
    }

    public void show() {
        mBottomSheetDialog.show();
    }

    public void dismiss() {
        mBottomSheetDialog.dismiss();
    }

    public interface OnEditorListener {
        void onDelete();

        /*void onChange();*/
    }

    @SuppressWarnings("unchecked")
    protected T getThis() {
        return (T) this;
    }

    protected abstract void setBottomSheetDialog();

    protected abstract void setDoneButton();

    protected abstract void setRunButton();

    protected void initView() {
        tilAppName = mBottomSheetDialog.findViewById(R.id.til_app_name);
        tietAppName = mBottomSheetDialog.findViewById(R.id.tiet_app_name);
        tietDescription = mBottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }

        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }
    }

    private void setOverlayButton() {
        ImageButton ibOverlay = mBottomSheetDialog.findViewById(R.id.ib_overlay);
        if (ibOverlay != null) {
            UiUtils.setVisibility(ibOverlay, isEditMode);
            ibOverlay.setOnClickListener(v -> startOverlay(TextUtils.getItemCommand(mItem)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void addShortcut(Context context, AnywhereEntity ae) {
        if (ShortcutsUtils.Singleton.INSTANCE.getInstance().getDynamicShortcuts().size() < 3) {
            AnywhereDialogBuilder builder = new AnywhereDialogBuilder(context);
            DialogManager.showAddShortcutDialog(context, builder, ae, (dialog, which) -> {
                ShortcutsUtils.addShortcut(ae);
                isShortcut = true;
                builder.setDismissParent(true);
            });
        } else {
            DialogManager.showCannotAddShortcutDialog(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void removeShortcut(Context context, AnywhereEntity ae) {
        DialogManager.showRemoveShortcutDialog(context, ae);
    }

    void setBottomSheetDialogImpl(Context context, @LayoutRes int layout) {
        View contentView = View.inflate(context, layout, null);
        mBottomSheetDialog.setContentView(contentView);
        mBottomSheetDialog.setDismissWithAnimation(true);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void startOverlay(String cmd) {
        if (PermissionUtils.checkOverlayPermission((Activity) mContext, -1)) {
            Intent intent = new Intent(mContext, OverlayService.class);
            intent.putExtra(OverlayService.COMMAND, OverlayService.COMMAND_OPEN);
            intent.putExtra(OverlayService.COMMAND_STR, cmd);
            String pkgName;
            if (mEditorType == URL_SCHEME) {
                pkgName = mItem.getParam2();
            } else {
                pkgName = mItem.getParam1();
            }
            intent.putExtra(OverlayService.PKG_NAME, pkgName);
            mContext.startService(intent);

            mBottomSheetDialog.dismiss();

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            mContext.startActivity(homeIntent);

            if (!Once.beenDone(OnceTag.OVERLAY_TIP)) {
                ToastUtil.makeText(R.string.toast_overlay_tip);
                Once.markDone(OnceTag.OVERLAY_TIP);
            }
        }
    }

    private void setMoreButton() {
        ImageButton ibMore = mBottomSheetDialog.findViewById(R.id.ib_editor_menu);
        if (ibMore != null) {
            UiUtils.setVisibility(ibMore, isEditMode);
            ibMore.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(mContext, ibMore);
                popup.getMenuInflater()
                        .inflate(R.menu.editor_menu, popup.getMenu());
                if (popup.getMenu() instanceof MenuBuilder) {
                    MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
                    menuBuilder.setOptionalIconsVisible(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    if (isShortcut) {
                        UiUtils.tintMenuIcon(mContext, popup.getMenu().getItem(0), R.color.colorAccent);
                        popup.getMenu().getItem(0).setTitle(R.string.dialog_remove_shortcut_title);
                    } else {
                        UiUtils.tintMenuIcon(mContext, popup.getMenu().getItem(0), R.color.textColorNormal);
                    }
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.add_shortcuts:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                if (!isShortcut) {
                                    Logger.d("add");
                                    addShortcut(mContext, mItem);
                                } else {
                                    Logger.d("remove");

                                    removeShortcut(mContext, mItem);
                                }
                            }
                            break;
                        case R.id.add_home_shortcuts:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                DialogManager.showCreatePinnedShortcutDialog((AppCompatActivity) mContext, mItem);
                            }
                            break;
                        case R.id.delete:
                            mListener.onDelete();
                            break;
                        case R.id.move_to_page:
                            DialogManager.showPageListDialog(mContext, mItem);
                            break;
                        case R.id.custom_color:
                            DialogManager.showColorPickerDialog(mContext, mItem);
                            break;
                        default:
                    }
                    return true;
                });

                popup.show();
            });
        }
    }

}

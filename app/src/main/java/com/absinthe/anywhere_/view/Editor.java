package com.absinthe.anywhere_.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import jonathanfinerty.once.Once;

public abstract class Editor<T extends Editor<?>> {
    public static final int ANYWHERE = 1;
    public static final int URL_SCHEME = 2;
    public static final int QR_CODE = 3;

    protected Context mContext;
    private OnEditorListener mListener;

    BottomSheetDialog mBottomSheetDialog;
    AnywhereEntity mItem;
    TextInputLayout tilAppName;
    TextInputEditText tietAppName, tietDescription;

    private int mEditorType;
    private boolean isExported;
    private boolean isShortcut;
    boolean isEditMode;

    Editor(Context context, int editorType) {
        mContext = context;
        mBottomSheetDialog = new BottomSheetDialog(context);
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
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtils.addShortcut(ae);
            dismiss();
        };

        MaterialAlertDialogBuilder addDialog = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_add_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show());

        MaterialAlertDialogBuilder cantAddDialog = new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> show());

        if (ShortcutsUtils.Singleton.INSTANCE.getInstance().getDynamicShortcuts().size() < 3) {
            addDialog.show();
        } else {
            cantAddDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void removeShortcut(Context context, AnywhereEntity ae) {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtils.removeShortcut(ae);
            dismiss();
        };

        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_remove_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> show())
                .show();
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
                                dismiss();
                                if (!isShortcut) {
                                    addShortcut(mContext, mItem);
                                } else {
                                    removeShortcut(mContext, mItem);
                                }
                            }
                            break;
                        case R.id.add_home_shortcuts:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ShortcutsUtils.addPinnedShortcut(mItem);
                            }
                            break;
                        case R.id.delete:
                            dismiss();
                            mListener.onDelete();
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

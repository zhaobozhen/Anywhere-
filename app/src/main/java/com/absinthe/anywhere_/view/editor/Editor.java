package com.absinthe.anywhere_.view.editor;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.OnceTag;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.services.OverlayService;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.view.AnywhereBottomSheetDialog;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import jonathanfinerty.once.Once;

public abstract class Editor<T extends Editor<?>> {
    public static final int ANYWHERE = 1;
    public static final int URL_SCHEME = 2;
    public static final int QR_CODE = 3;
    public static final int IMAGE = 4;
    public static final int SHELL = 5;
    public static final int SWITCH_SHELL = 6;

    protected Context mContext;
    private OnEditorListener mListener;

    AnywhereBottomSheetDialog mBottomSheetDialog;
    AnywhereEntity mItem;

    ViewGroup container;
    ImageButton ibRun;
    MaterialButton btnDone;
    ImageButton ibOverlay, ibMore;

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

    public T setDismissParent(boolean flag) {
        mBottomSheetDialog.setDismissParent(flag);
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

    protected abstract void setBottomSheetDialog();

    protected abstract void setDoneButton();

    protected abstract void setRunButton();

    protected void initView() {
        ibOverlay = container.findViewById(R.id.ib_overlay);
        ibRun = container.findViewById(R.id.ib_trying_run);
        ibMore = container.findViewById(R.id.ib_editor_menu);
        btnDone = container.findViewById(R.id.btn_edit_anywhere_done);
    }

    @SuppressWarnings("unchecked")
    private T getThis() {
        return (T) this;
    }

    protected void setOverlayButton() {
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
            DialogManager.showCannotAddShortcutDialog(context, (dialog, which) -> {
                ShortcutsUtils.addShortcut(ae);
                isShortcut = true;
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void removeShortcut(Context context, AnywhereEntity ae) {
        DialogManager.showRemoveShortcutDialog(context, ae);
    }

    void setBottomSheetDialogImpl(Context context, @LayoutRes int layout) {
        int frameRes = R.layout.layout_editor_frame;
        if (mEditorType == URL_SCHEME) {
            frameRes = R.layout.layout_url_scheme_editor_frame;
        } else if (mEditorType == IMAGE) {
            frameRes = R.layout.layout_image_editor_frame;
        }

        container = (ViewGroup) View.inflate(context, frameRes, null);

        mBottomSheetDialog.setContentView(container);
        mBottomSheetDialog.setDismissWithAnimation(true);
        View parent = (View) container.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        container = container.findViewById(R.id.container);
        View contentView = View.inflate(context, layout, null);
        container.addView(contentView, 1);
    }

    private void startOverlay(String cmd) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startOverlayImpl(cmd);
        } else {
            if (Build.VERSION.SDK_INT >= 30) {
                ToastUtil.makeText(R.string.toast_overlay_choose_anywhere);
            }
            PermissionUtils.requestDrawOverlays(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    startOverlayImpl(cmd);
                }

                @Override
                public void onDenied() {

                }
            });
        }
    }

    private void startOverlayImpl(String cmd) {
        Intent intent = new Intent(mContext, OverlayService.class);
        intent.putExtra(OverlayService.COMMAND_STR, cmd);
        intent.putExtra(OverlayService.PKG_NAME, mItem.getPackageName());
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

    @SuppressLint("RestrictedApi")
    private void setMoreButton() {
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
                        popup.getMenu().findItem(R.id.add_shortcuts).setTitle(R.string.dialog_remove_shortcut_title);
                    } else {
                        UiUtils.tintMenuIcon(mContext, popup.getMenu().getItem(0), R.color.textColorNormal);
                    }
                }
                if (this instanceof ImageEditor) {
                    popup.getMenu().findItem(R.id.share_card).setVisible(false);
                } else if (this instanceof SwitchShellEditor) {
                    popup.getMenu().findItem(R.id.add_shortcuts).setEnabled(false);
                    popup.getMenu().findItem(R.id.add_home_shortcuts).setEnabled(false);
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.add_shortcuts:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                if (!isShortcut) {
                                    addShortcut(mContext, mItem);
                                } else {
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
                        case R.id.share_card:
                            DialogManager.showCardSharingDialog((AppCompatActivity) mContext, TextUtils.genCardSharingUrl(mItem));
                            break;
                        case R.id.custom_icon:
                            ((BaseActivity) mContext).setDocumentResultListener(uri -> {
                                mItem.setIconUri(uri.toString());
                                AnywhereApplication.sRepository.update(mItem);
                                dismiss();
                            });

                            try {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                ((BaseActivity) mContext).startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                ToastUtil.makeText(R.string.toast_no_document_app);
                            }
                            break;
                        case R.id.restore_icon:
                            mItem.setIconUri("");
                            AnywhereApplication.sRepository.update(mItem);
                            dismiss();
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

package com.absinthe.anywhere_.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Editor {
    public static final int ANYWHERE = 1;
    public static final int URL_SCHEME = 2;

    private Context mContext;
    private BottomSheetDialog mBottomSheetDialog;
    private AnywhereEntity mItem;
    private OnEditorListener mListener;

    private int mEditorType;
    private boolean isShortcut;
    private boolean isEditMode;
    private boolean isExported;

    public Editor(Context context, int editorType) {
        mContext = context;
        mBottomSheetDialog = new BottomSheetDialog(context);
        mEditorType = editorType;
        isShortcut = false;
        isEditMode = false;
        isExported = false;

        View contentView = View.inflate(context, R.layout.bottom_sheet_dialog_anywhere, null);
        if (mEditorType == ANYWHERE) {
            contentView = View.inflate(context, R.layout.bottom_sheet_dialog_anywhere, null);
        } else if (mEditorType == URL_SCHEME) {
            contentView = View.inflate(context, R.layout.bottom_sheet_dialog_url_scheme, null);
        }

        mBottomSheetDialog.setContentView(contentView);
        mBottomSheetDialog.setDismissWithAnimation(true);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public Editor isShortcut(boolean flag) {
        isShortcut = flag;
        return this;
    }

    public Editor isEditorMode(boolean flag) {
        isEditMode = flag;
        return this;
    }

    public Editor isExported(boolean flag) {
        isExported = flag;
        return this;
    }

    public Editor item(AnywhereEntity item) {
        mItem = item;
        return this;
    }

    public Editor build() {
        if (mEditorType == ANYWHERE) {
            TextInputLayout tilAppName = mBottomSheetDialog.findViewById(R.id.til_app_name);
            TextInputLayout tilPackageName = mBottomSheetDialog.findViewById(R.id.til_package_name);
            TextInputLayout tilClassName = mBottomSheetDialog.findViewById(R.id.til_class_name);

            TextInputEditText tietAppName = mBottomSheetDialog.findViewById(R.id.tiet_app_name);
            TextInputEditText tietPackageName = mBottomSheetDialog.findViewById(R.id.tiet_package_name);
            TextInputEditText tietClassName = mBottomSheetDialog.findViewById(R.id.tiet_class_name);
            TextInputEditText tietDescription = mBottomSheetDialog.findViewById(R.id.tiet_description);
            TextInputEditText tietIntentExtra = mBottomSheetDialog.findViewById(R.id.tiet_intent_extra);

            if (tietAppName != null) {
                tietAppName.setText(mItem.getAppName());
            }

            if (tietPackageName != null) {
                tietPackageName.setText(mItem.getParam1());
            }

            if (tietClassName != null) {
                tietClassName.setText(mItem.getParam2());
            }

            if (tietDescription != null) {
                tietDescription.setText(mItem.getDescription());
            }

            if (tietIntentExtra != null) {
                tietIntentExtra.setText(mItem.getParam3());
            }

            Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
            if (btnEditAnywhereDone != null) {
                btnEditAnywhereDone.setOnClickListener(view -> {
                    if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null && tietIntentExtra != null) {
                        String pName = tietPackageName.getText() == null ? mItem.getParam1() : tietPackageName.getText().toString();
                        String cName = tietClassName.getText() == null ? mItem.getParam2() : tietClassName.getText().toString();
                        String aName = tietAppName.getText() == null ? mItem.getAppName() : tietAppName.getText().toString();
                        String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                        String iExtra = tietIntentExtra.getText() == null ? "" : tietIntentExtra.getText().toString();

                        if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                            tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                        }
                        if (tietPackageName.getText().toString().isEmpty() && tilPackageName != null) {
                            tilPackageName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                        }
                        if (tietClassName.getText().toString().isEmpty() && tilClassName != null) {
                            tilClassName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                        }

                        if (!tietAppName.getText().toString().isEmpty()
                                && !tietPackageName.getText().toString().isEmpty()
                                && !tietClassName.getText().toString().isEmpty()) {
                            AnywhereEntity ae = new AnywhereEntity(mItem.getId(), aName, pName, cName, iExtra,
                                    desc, mItem.getType(), mItem.getTimeStamp());
                            if (isEditMode) {
                                if (!aName.equals(mItem.getAppName()) || !pName.equals(mItem.getParam1()) || !cName.equals(mItem.getParam2())) {
                                    if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                            ShortcutsUtil.removeShortcut(mItem);
                                            ShortcutsUtil.addShortcut(ae);
                                        }
                                    }
                                }
                                MainFragment.getViewModelInstance().update(ae);
                            } else {
                                if (EditUtils.hasSameAppName(pName, cName)) {
                                    dismiss();
                                    new MaterialAlertDialogBuilder(mContext)
                                            .setMessage(R.string.dialog_message_same_app_name)
                                            .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                                                MainFragment.getViewModelInstance().insert(ae);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                                    ShortcutsUtil.removeShortcut(Objects.requireNonNull(EditUtils.hasSameAppNameEntity(mItem.getParam1(), mItem.getParam2())));
                                                }
                                            })
                                            .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show())
                                            .show();
                                } else {
                                    MainFragment.getViewModelInstance().insert(ae);
                                }
                            }
                            dismiss();
                        }

                    } else {
                        ToastUtil.makeText("error data.");
                    }
                });
            }

            ImageButton ibRun = mBottomSheetDialog.findViewById(R.id.ib_trying_run);
            if (ibRun != null) {
                ibRun.setOnClickListener(view -> {
                    if (tietPackageName != null && tietClassName != null && tietIntentExtra != null) {
                        String pName = tietPackageName.getText() == null ? mItem.getParam1() : tietPackageName.getText().toString();
                        String cName = tietClassName.getText() == null ? mItem.getParam2() : tietClassName.getText().toString();
                        String iExtra = tietIntentExtra.getText() == null ? "" : tietIntentExtra.getText().toString();

                        if (!tietPackageName.getText().toString().isEmpty()
                                && !tietClassName.getText().toString().isEmpty()) {
                            AnywhereEntity ae = new AnywhereEntity(mItem.getId(), "", pName, cName, iExtra,
                                    "", mItem.getType(), mItem.getTimeStamp());//Todo param3
                            PermissionUtil.execCmd(TextUtils.getItemCommand(ae));
                        }
                    }
                });
            }

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
                            popup.getMenu().getItem(0).setIcon(R.drawable.ic_added_shortcut);
                            popup.getMenu().getItem(0).setTitle(R.string.dialog_remove_shortcut_title);
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
                                    ShortcutsUtil.addPinnedShortcut(mItem);
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

        } else if (mEditorType == URL_SCHEME) {
            TextInputLayout tilAppName = mBottomSheetDialog.findViewById(R.id.til_app_name);
            TextInputLayout tilUrlScheme = mBottomSheetDialog.findViewById(R.id.til_url_scheme);

            TextInputEditText tietAppName = mBottomSheetDialog.findViewById(R.id.tiet_app_name);
            TextInputEditText tietUrlScheme = mBottomSheetDialog.findViewById(R.id.tiet_url_scheme);
            TextInputEditText tietDescription = mBottomSheetDialog.findViewById(R.id.tiet_description);

            if (tietAppName != null) {
                tietAppName.setText(mItem.getAppName());
            }
            if (tietUrlScheme != null) {
                tietUrlScheme.setText(mItem.getParam1());
            }
            if (tietDescription != null) {
                tietDescription.setText(mItem.getDescription());
            }

            Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
            if (btnEditAnywhereDone != null) {
                btnEditAnywhereDone.setOnClickListener(view -> {
                    if (tietUrlScheme != null && tietAppName != null && tietDescription != null) {
                        String uScheme = tietUrlScheme.getText() == null ? "" : tietUrlScheme.getText().toString();
                        String aName = tietAppName.getText() == null  ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                        String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                        if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                            tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                        }
                        if (tietUrlScheme.getText().toString().isEmpty() && tilUrlScheme != null) {
                            tilUrlScheme.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                        }

                        if (!tietAppName.getText().toString().isEmpty()
                                && !tietUrlScheme.getText().toString().isEmpty()) {
                            AnywhereEntity ae = new AnywhereEntity(mItem.getId(), aName, uScheme, null, null
                                    , desc, mItem.getType(), mItem.getTimeStamp());

                            if (isEditMode) {
                                if (!aName.equals(mItem.getAppName()) || !uScheme.equals(mItem.getParam1())) {
                                    if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                            ShortcutsUtil.removeShortcut(mItem);
                                            ShortcutsUtil.addShortcut(ae);
                                        }
                                    }
                                }
                                MainFragment.getViewModelInstance().update(ae);
                            } else {
                                if (EditUtils.hasSameAppName(uScheme)) {
                                    dismiss();
                                    new MaterialAlertDialogBuilder(mContext)
                                            .setMessage(R.string.dialog_message_same_app_name)
                                            .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                                                MainFragment.getViewModelInstance().insert(ae);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                                    ShortcutsUtil.removeShortcut(EditUtils.hasSameAppNameEntity(mItem.getParam1()));
                                                }
                                            })
                                            .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show())
                                            .show();
                                } else {
                                    MainFragment.getViewModelInstance().insert(ae);
                                }
                            }
                            dismiss();
                        }
                    } else {
                        ToastUtil.makeText("error data.");
                    }
                });
            }

            Button btnUrlSchemeCommunity = mBottomSheetDialog.findViewById(R.id.btn_url_scheme_community);
            if (btnUrlSchemeCommunity != null) {
                btnUrlSchemeCommunity.setOnClickListener(view -> {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://sharecuts.cn/apps"));
                    mContext.startActivity(intent);
                });
            }

            ImageButton ibRun = mBottomSheetDialog.findViewById(R.id.ib_trying_run);
            if (ibRun != null) {
                ibRun.setOnClickListener(view -> {
                    if (tietUrlScheme != null) {
                        String uName = tietUrlScheme.getText() == null ? mItem.getParam1() : tietUrlScheme.getText().toString();

                        if (!tietUrlScheme.getText().toString().isEmpty()) {
                            AnywhereEntity ae = new AnywhereEntity(mItem.getId(), "", uName, "", "",
                                    "", mItem.getType(), mItem.getTimeStamp());
                            PermissionUtil.execCmd(TextUtils.getItemCommand(ae));
                        }
                    }
                });
            }

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
                            popup.getMenu().getItem(0).setIcon(R.drawable.ic_added_shortcut);
                            popup.getMenu().getItem(0).setTitle(R.string.dialog_remove_shortcut_title);
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
                                    ShortcutsUtil.addPinnedShortcut(mItem);
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
        return this;
    }

    public Editor setOnEditorListener(OnEditorListener listener) {
        mListener = listener;
        return this;
    }

    public void show() {
        mBottomSheetDialog.show();
    }

    public void dismiss() {
        mBottomSheetDialog.dismiss();
    }

    public interface OnEditorListener {
        void onDelete();
//        void onChange();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void addShortcut(Context context, AnywhereEntity ae) {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtil.addShortcut(ae);
            dismiss();
        };

        MaterialAlertDialogBuilder addDialog =  new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_add_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show());

        MaterialAlertDialogBuilder cantAddDialog =  new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> show());

        if (ShortcutsUtil.Singleton.INSTANCE.getInstance().getDynamicShortcuts().size() < 3) {
            addDialog.show();
        } else {
            cantAddDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void removeShortcut(Context context, AnywhereEntity ae) {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtil.removeShortcut(ae);
            dismiss();
        };

        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_remove_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> show())
                .show();
    }

}

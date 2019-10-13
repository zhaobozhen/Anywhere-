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

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.ToastUtil;
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

    public Editor(Context context, int editorType) {
        mContext = context;
        mBottomSheetDialog = new BottomSheetDialog(context);
        mEditorType = editorType;
        isShortcut = false;
        isEditMode = false;

        View contentView = View.inflate(context, R.layout.bottom_sheet_dialog_content, null);
        if (mEditorType == ANYWHERE) {
            contentView = View.inflate(context, R.layout.bottom_sheet_dialog_content, null);
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

            Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
            if (btnEditAnywhereDone != null) {
                btnEditAnywhereDone.setOnClickListener(view -> {
                    if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null) {
                        String pName = tietPackageName.getText() == null ? mItem.getParam1() : tietPackageName.getText().toString();
                        String cName = tietClassName.getText() == null ? mItem.getParam2() : tietClassName.getText().toString();
                        String aName = tietAppName.getText() == null ? mItem.getAppName() : tietAppName.getText().toString();
                        String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

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
                            AnywhereEntity ae = new AnywhereEntity(mItem.getId(), aName, pName, cName, mItem.getParam3()
                                    , desc, mItem.getType(), System.currentTimeMillis() + "");
                            if (isEditMode) {
                                if (!aName.equals(mItem.getAppName()) || !pName.equals(mItem.getParam1()) || !cName.equals(mItem.getParam2())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtil.removeShortcut(mItem);
                                        ShortcutsUtil.addShortcut(ae);
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

            ImageButton ibDelete = mBottomSheetDialog.findViewById(R.id.ib_delete_anywhere);
            if (ibDelete != null) {
                if (isEditMode) {
                    ibDelete.setVisibility(View.VISIBLE);
                } else {
                    ibDelete.setVisibility(View.GONE);
                }
                ibDelete.setOnClickListener(view -> {
                    dismiss();
                    mListener.onDelete();
                });
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ImageButton ibAddShortcut = mBottomSheetDialog.findViewById(R.id.ib_add_shortcut);
                if (ibAddShortcut != null) {
                    if (isEditMode) {
                        ibAddShortcut.setVisibility(View.VISIBLE);
                        if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                            ibAddShortcut.setBackgroundResource(R.drawable.ic_added_shortcut);
                        }
                    } else {
                        ibAddShortcut.setVisibility(View.GONE);
                    }
                    ibAddShortcut.setOnClickListener(view -> {
                        dismiss();
                        if (mItem.getShortcutType() != AnywhereType.SHORTCUTS) {
                            addShortcut(mContext, mItem);
                        } else {
                            removeShortcut(mContext, mItem);
                        }
                        mListener.onChange();
                    });
                }
            }

        } else if (mEditorType == URL_SCHEME) {
            TextInputLayout tilAppName = mBottomSheetDialog.findViewById(R.id.til_app_name);
            TextInputLayout tilUrlScheme = mBottomSheetDialog.findViewById(R.id.til_url_scheme);

            TextInputEditText tietAppName = mBottomSheetDialog.findViewById(R.id.tiet_app_name);
            TextInputEditText tietUrlScheme = mBottomSheetDialog.findViewById(R.id.tiet_url_scheme);
            TextInputEditText tietDescription = mBottomSheetDialog.findViewById(R.id.tiet_description);

            if (tietAppName != null) {
                tietAppName.setText(R.string.bsd_new_url_scheme_name);
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
                                    , desc, mItem.getType(), System.currentTimeMillis() + "");

                            if (isEditMode) {
                                if (!aName.equals(mItem.getAppName()) || !uScheme.equals(mItem.getParam1())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtil.removeShortcut(mItem);
                                        ShortcutsUtil.addShortcut(ae);
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
                                                    ShortcutsUtil.removeShortcut(Objects.requireNonNull(EditUtils.hasSameAppNameEntity(mItem.getParam1())));
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

            ImageButton ibDelete = mBottomSheetDialog.findViewById(R.id.ib_delete_anywhere);
            if (ibDelete != null) {
                if (isEditMode) {
                    ibDelete.setVisibility(View.VISIBLE);
                } else {
                    ibDelete.setVisibility(View.GONE);
                }
                ibDelete.setOnClickListener(view -> {
                    dismiss();
                    mListener.onDelete();
                });
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ImageButton ibAddShortcut = mBottomSheetDialog.findViewById(R.id.ib_add_shortcut);
                if (ibAddShortcut != null) {
                    if (isEditMode) {
                        ibAddShortcut.setVisibility(View.VISIBLE);
                        if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                            ibAddShortcut.setBackgroundResource(R.drawable.ic_added_shortcut);
                        }
                    } else {
                        ibAddShortcut.setVisibility(View.GONE);
                    }
                    ibAddShortcut.setOnClickListener(view -> {
                        dismiss();
                        if (mItem.getShortcutType() != AnywhereType.SHORTCUTS) {
                            addShortcut(mContext, mItem);
                        } else {
                            removeShortcut(mContext, mItem);
                        }
                        mListener.onChange();
                    });
                }
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
        void onChange();
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
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show());

        MaterialAlertDialogBuilder cantAddDialog =  new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> show());

        if (ShortcutsUtil.getInstance().getDynamicShortcuts().size() < 3) {
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
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> show())
                .show();
    }

}

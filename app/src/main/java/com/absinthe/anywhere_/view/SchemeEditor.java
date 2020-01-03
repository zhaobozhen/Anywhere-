package com.absinthe.anywhere_.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageButton;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SchemeEditor extends Editor<SchemeEditor> {

    private TextInputLayout tilUrlScheme;
    private TextInputEditText tietUrlScheme;

    public SchemeEditor(Context context) {
        super(context, Editor.URL_SCHEME);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_url_scheme);
    }

    @Override
    protected void initView() {
        super.initView();

        tilUrlScheme = mBottomSheetDialog.findViewById(R.id.til_url_scheme);
        tietUrlScheme = mBottomSheetDialog.findViewById(R.id.tiet_url_scheme);

        if (tietUrlScheme != null) {
            tietUrlScheme.setText(mItem.getParam1());
        }

        Button btnUrlSchemeCommunity = mBottomSheetDialog.findViewById(R.id.btn_url_scheme_community);
        if (btnUrlSchemeCommunity != null) {
            btnUrlSchemeCommunity.setOnClickListener(view -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://sharecuts.cn/apps"));
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_no_react_url);
                }
            });
        }
    }

    @Override
    protected void setDoneButton() {
        Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietUrlScheme != null && tietAppName != null && tietDescription != null) {
                    String uScheme = tietUrlScheme.getText() == null ? "" : tietUrlScheme.getText().toString();
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietUrlScheme.getText().toString().isEmpty() && tilUrlScheme != null) {
                        tilUrlScheme.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietUrlScheme.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                                ae.setId(mItem.getId());
                                ae.setAppName(aName);
                                ae.setParam1(uScheme);
                                ae.setParam2(UiUtils.getPkgNameByUrl(mContext, uScheme));
                                ae.setDescription(desc);
                                ae.setType(mItem.getType());
                                ae.setCategory(mItem.getCategory());
                                ae.setTimeStamp(mItem.getTimeStamp());

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName()) || !uScheme.equals(mItem.getParam1())) {
                                if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.removeShortcut(mItem);
                                        ShortcutsUtils.addShortcut(ae);
                                    }
                                }
                            }
                            AnywhereApplication.sRepository.update(ae);
                        } else {
                            if (EditUtils.hasSameAppName(uScheme)) {
                                dismiss();
                                new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                                        .setMessage(R.string.dialog_message_same_app_name)
                                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                                            AnywhereApplication.sRepository.insert(ae);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                                ShortcutsUtils.removeShortcut(EditUtils.hasSameAppNameEntity(mItem.getParam1()));
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> show())
                                        .show();
                            } else {
                                AnywhereApplication.sRepository.insert(ae);
                            }
                        }
                        dismiss();
                    }
                } else {
                    ToastUtil.makeText("error data.");
                }
            });
        }
    }

    @Override
    protected void setRunButton() {
        ImageButton ibRun = mBottomSheetDialog.findViewById(R.id.ib_trying_run);
        if (ibRun != null) {
            ibRun.setOnClickListener(view -> {
                if (tietUrlScheme != null) {
                    String uName = tietUrlScheme.getText() == null ? mItem.getParam1() : tietUrlScheme.getText().toString();

                    if (!tietUrlScheme.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                                ae.setId(mItem.getId());
                                ae.setParam1(uName);
                                ae.setType(mItem.getType());
                                ae.setCategory(mItem.getCategory());
                                ae.setTimeStamp(mItem.getTimeStamp());

                        CommandUtils.execCmd(TextUtils.getItemCommand(ae));
                    }
                }
            });
        }
    }
}

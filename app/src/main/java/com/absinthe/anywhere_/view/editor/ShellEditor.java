package com.absinthe.anywhere_.view.editor;

import android.content.Context;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
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
import com.absinthe.anywhere_.utils.manager.DialogManager;

public class ShellEditor extends Editor<ShellEditor> {

    private EditText etShellContent;

    public ShellEditor(Context context) {
        super(context, Editor.SHELL);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_shell);
    }

    @Override
    protected void initView() {
        super.initView();

        etShellContent = mBottomSheetDialog.findViewById(R.id.et_shell_content);
        if (etShellContent != null) {
            etShellContent.setText(mItem.getParam1());
            etShellContent.requestFocus();
        }
    }

    @Override
    protected void setDoneButton() {
        Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (etShellContent != null && tietAppName != null && tietDescription != null) {
                    String shell = etShellContent.getText() == null ? "" : etShellContent.getText().toString();
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (etShellContent.getText().toString().isEmpty()) {
                        etShellContent.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !etShellContent.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setId(mItem.getId());
                        ae.setAppName(aName);
                        ae.setParam1(shell);
                        ae.setDescription(desc);
                        ae.setType(mItem.getType());
                        ae.setCategory(mItem.getCategory());
                        ae.setTimeStamp(mItem.getTimeStamp());
                        ae.setColor(mItem.getColor());

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName()) || !shell.equals(mItem.getParam1())) {
                                if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.removeShortcut(mItem);
                                        ShortcutsUtils.addShortcut(ae);
                                    }
                                }
                            }
                            AnywhereApplication.sRepository.update(ae);
                        } else {
                            if (EditUtils.hasSameAppName(shell)) {
                                DialogManager.showHasSameCardDialog(mContext, (dialog, which) -> {
                                    AnywhereApplication.sRepository.insert(ae);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.removeShortcut(EditUtils.hasSameAppNameEntity(mItem.getParam1()));
                                    }
                                });
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
                if (etShellContent != null) {
                    String shell = etShellContent.getText() == null ? mItem.getParam1() : etShellContent.getText().toString();

                    if (!etShellContent.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setId(mItem.getId());
                        ae.setParam1(shell);
                        ae.setType(mItem.getType());
                        ae.setCategory(mItem.getCategory());
                        ae.setTimeStamp(mItem.getTimeStamp());

                        CommandUtils.execAdbCmd(TextUtils.getItemCommand(ae));
                    }
                }
            });
        }
    }
}

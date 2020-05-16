package com.absinthe.anywhere_.view.editor;

import android.content.Context;
import android.os.Build;
import android.widget.EditText;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ShellEditor extends Editor<ShellEditor> {

    private TextInputLayout tilAppName;
    private TextInputEditText tietAppName, tietDescription;
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

        tilAppName = container.findViewById(R.id.til_app_name);
        tietAppName = container.findViewById(R.id.tiet_app_name);
        tietDescription = container.findViewById(R.id.tiet_description);
        etShellContent = container.findViewById(R.id.et_shell_content);

        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }

        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }

        if (etShellContent != null) {
            etShellContent.setText(mItem.getParam1());
            etShellContent.requestFocus();
        }
    }

    @Override
    protected void setDoneButton() {
        if (btnDone != null) {
            btnDone.setOnClickListener(view -> {
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
                        AnywhereEntity ae = new AnywhereEntity(mItem);
                        ae.setAppName(aName);
                        ae.setParam1(shell);
                        ae.setDescription(desc);

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName()) || !shell.equals(mItem.getParam1())) {
                                if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.updateShortcut(mItem);
                                    }
                                }
                            }
                            AnywhereApplication.sRepository.update(ae);
                        } else {
                            AnywhereApplication.sRepository.insert(ae);
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

                        String result = CommandUtils.execAdbCmd(ae.getParam1());
                        DialogManager.showShellResultDialog(mContext, result, null, null);
                    }
                }
            });
        }
    }
}

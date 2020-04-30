package com.absinthe.anywhere_.view.editor;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SwitchShellEditor extends Editor<SwitchShellEditor> {

    public static final String SWITCH_SHELL_OFF_STATUS = "off";
    public static final String SWITCH_SHELL_ON_STATUS = "on";

    private TextInputLayout tilAppName, tilSwitchOn, tilSwitchOff;
    private TextInputEditText tietAppName, tietDescription, tietSwitchOn, tietSwitchOff;

    public SwitchShellEditor(Context context) {
        super(context, Editor.SWITCH_SHELL);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_switch_shell);
    }

    @Override
    protected void initView() {
        super.initView();

        tilAppName = container.findViewById(R.id.til_app_name);
        tilSwitchOn = container.findViewById(R.id.til_switch_on);
        tilSwitchOff = container.findViewById(R.id.til_switch_off);

        tietAppName = container.findViewById(R.id.tiet_app_name);
        tietDescription = container.findViewById(R.id.tiet_description);
        tietSwitchOn = container.findViewById(R.id.tiet_switch_on);
        tietSwitchOff = container.findViewById(R.id.tiet_switch_off);

        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }

        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }

        if (tietSwitchOn != null) {
            tietSwitchOn.setText(mItem.getParam1());
        }

        if (tietSwitchOff != null) {
            tietSwitchOff.setText(mItem.getParam2());
        }

        ibRun.setVisibility(View.GONE);
    }

    @Override
    protected void setDoneButton() {
        if (btnDone != null) {
            btnDone.setOnClickListener(view -> {
                if (tietSwitchOn != null && tietSwitchOff != null && tietAppName != null && tietDescription != null) {
                    String shellOn = tietSwitchOn.getText() == null ? "" : tietSwitchOn.getText().toString();
                    String shellOff = tietSwitchOff.getText() == null ? "" : tietSwitchOff.getText().toString();
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietSwitchOn.getText().toString().isEmpty()) {
                        tilSwitchOn.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietSwitchOff.getText().toString().isEmpty()) {
                        tilSwitchOff.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietSwitchOn.getText().toString().isEmpty()
                            && !tietSwitchOff.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setId(mItem.getId());
                        ae.setAppName(aName);
                        ae.setParam1(shellOn);
                        ae.setParam2(shellOff);
                        ae.setParam3(SWITCH_SHELL_OFF_STATUS);
                        ae.setDescription(desc);
                        ae.setType(mItem.getType());
                        ae.setCategory(mItem.getCategory());
                        ae.setTimeStamp(mItem.getTimeStamp());
                        ae.setColor(mItem.getColor());

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName()) || !shellOn.equals(mItem.getParam1()) || !shellOff.equals(mItem.getParam2())) {
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

    }
}

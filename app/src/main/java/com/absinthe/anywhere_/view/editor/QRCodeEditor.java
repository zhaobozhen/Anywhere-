package com.absinthe.anywhere_.view.editor;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class QRCodeEditor extends Editor<QRCodeEditor> {

    private TextInputLayout tilAppName;
    private TextInputEditText tietAppName, tietDescription;

    public QRCodeEditor(Context context) {
        super(context, Editor.QR_CODE);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_qr_code);
    }

    @Override
    protected void initView() {
        super.initView();
        tilAppName = container.findViewById(R.id.til_app_name);
        tietAppName = container.findViewById(R.id.tiet_app_name);
        tietDescription = container.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }
        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }
    }

    @Override
    protected void setDoneButton() {
        if (btnDone != null) {
            btnDone.setOnClickListener(view -> {
                if (tietAppName != null && tietDescription != null) {
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setAppName(aName);
                        ae.setParam1(mItem.getParam1());
                        ae.setParam2(mItem.getId());
                        ae.setDescription(desc);
                        ae.setType(mItem.getType());
                        ae.setColor(mItem.getColor());

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName())) {
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
        ibRun.setVisibility(View.GONE);
    }
}

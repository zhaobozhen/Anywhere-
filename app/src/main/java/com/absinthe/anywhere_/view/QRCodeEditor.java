package com.absinthe.anywhere_.view;

import android.content.Context;
import android.os.Build;
import android.widget.Button;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.ToastUtil;

public class QRCodeEditor extends Editor<QRCodeEditor> {

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
    }

    @Override
    protected void setDoneButton() {
        Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietAppName != null && tietDescription != null) {
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder()
                                .setAppName(aName)
                                .setParam1(mItem.getParam1())
                                .setParam2(mItem.getId())
                                .setDescription(desc)
                                .setType(mItem.getType())
                                .setCategory(mItem.getCategory());

                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName())) {
                                if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.removeShortcut(mItem);
                                        ShortcutsUtils.addShortcut(ae);
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

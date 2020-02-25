package com.absinthe.anywhere_.view.editor;

import android.content.Context;
import android.os.Build;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AnywhereEditor extends Editor<AnywhereEditor> {

    private TextInputLayout tilAppName, tilPackageName, tilClassName;
    private TextInputEditText tietAppName, tietPackageName, tietClassName, tietIntentExtra, tietDescription;

    public AnywhereEditor(Context context) {
        super(context, Editor.ANYWHERE);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_anywhere);
    }

    @Override
    protected void initView() {
        super.initView();

        tilAppName = container.findViewById(R.id.til_app_name);
        tilPackageName = container.findViewById(R.id.til_package_name);
        tilClassName = container.findViewById(R.id.til_class_name);

        tietAppName = container.findViewById(R.id.tiet_app_name);
        tietPackageName = container.findViewById(R.id.tiet_package_name);
        tietClassName = container.findViewById(R.id.tiet_class_name);
        tietIntentExtra = container.findViewById(R.id.tiet_intent_extra);
        tietDescription = container.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }

        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }

        if (tietPackageName != null) {
            tietPackageName.setText(mItem.getParam1());
        }

        if (tietClassName != null) {
            tietClassName.setText(mItem.getParam2());
        }

        if (tietIntentExtra != null) {
            tietIntentExtra.setText(mItem.getParam3());
        }
    }

    @Override
    protected void setDoneButton() {
        if (btnDone != null) {
            btnDone.setOnClickListener(view -> {
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
                        AnywhereEntity ae = new AnywhereEntity(mItem);
                        ae.setAppName(aName);
                        ae.setParam1(pName);
                        ae.setParam2(cName);
                        ae.setParam3(iExtra);
                        ae.setDescription(desc);
                        ae.setCategory(GlobalValues.sCategory);
                        if (isEditMode) {
                            if (!aName.equals(mItem.getAppName()) || !pName.equals(mItem.getParam1()) || !cName.equals(mItem.getParam2())) {
                                if (mItem.getShortcutType() == AnywhereType.SHORTCUTS) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                        ShortcutsUtils.removeShortcut(mItem);
                                        ShortcutsUtils.addShortcut(ae);
                                    }
                                }
                            }
                            AnywhereApplication.sRepository.update(ae);
                        } else {
                            if (EditUtils.hasSameAppName(pName, cName)) {
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
                    ToastUtil.makeText("Error data");
                }
            });
        }
    }

    @Override
    protected void setRunButton() {
        if (ibRun != null) {
            ibRun.setOnClickListener(view -> {
                if (tietPackageName != null && tietClassName != null && tietIntentExtra != null) {
                    String pName = tietPackageName.getText() == null ? mItem.getParam1() : tietPackageName.getText().toString();
                    String cName = tietClassName.getText() == null ? mItem.getParam2() : tietClassName.getText().toString();
                    String iExtra = tietIntentExtra.getText() == null ? "" : tietIntentExtra.getText().toString();

                    if (!tietPackageName.getText().toString().isEmpty()
                            && !tietClassName.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setId(mItem.getId());
                        ae.setParam1(pName);
                        ae.setParam2(cName);
                        ae.setParam3(iExtra);//Todo param3
                        ae.setType(mItem.getType());
                        ae.setTimeStamp(mItem.getTimeStamp());

                        CommandUtils.execCmd(TextUtils.getItemCommand(ae));
                    }
                }
            });
        }
    }
}

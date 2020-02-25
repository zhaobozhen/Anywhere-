package com.absinthe.anywhere_.view.editor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.fragment.DynamicParamsDialogFragment;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SchemeEditor extends Editor<SchemeEditor> {

    private TextInputLayout tilUrlScheme;
    private TextInputEditText tietUrlScheme;
    private TextInputLayout tilDynamicParams;
    private TextInputEditText tietDynamicParams;

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
        tilDynamicParams = mBottomSheetDialog.findViewById(R.id.til_dynamic_params);
        tietDynamicParams = mBottomSheetDialog.findViewById(R.id.tiet_dynamic_params);

        if (tietUrlScheme != null) {
            tietUrlScheme.setText(mItem.getParam1());
        }

        if (tietDynamicParams != null && !TextUtils.isEmpty(mItem.getParam3())) {
            tietDynamicParams.setText(mItem.getParam3());
        }

        Button btnUrlSchemeCommunity = mBottomSheetDialog.findViewById(R.id.btn_url_scheme_community);
        if (btnUrlSchemeCommunity != null) {
            btnUrlSchemeCommunity.setOnClickListener(view -> {
                try {
                    URLSchemeHandler.parse(URLManager.SHORTCUT_COMMUNITY_PAGE, mContext);
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
                if (tietUrlScheme != null && tietAppName != null && tietDescription != null && tietDynamicParams != null) {
                    String uScheme = tietUrlScheme.getText() == null ? "" : tietUrlScheme.getText().toString();
                    String aName = tietAppName.getText() == null ? mContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                    String dynamic = tietDynamicParams.getText() == null ? "" : tietDynamicParams.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietUrlScheme.getText().toString().isEmpty() && tilUrlScheme != null) {
                        tilUrlScheme.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietUrlScheme.getText().toString().isEmpty()) {
                        AnywhereEntity ae = new AnywhereEntity(mItem);
                        ae.setAppName(aName);
                        ae.setParam1(uScheme);
                        ae.setParam2(UiUtils.getPkgNameByUrl(mContext, uScheme));
                        ae.setParam3(dynamic);
                        ae.setDescription(desc);

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
        ImageButton ibRun = mBottomSheetDialog.findViewById(R.id.ib_trying_run);
        if (ibRun != null) {
            ibRun.setOnClickListener(view -> {
                if (tietUrlScheme != null) {
                    String uName = tietUrlScheme.getText() == null ? mItem.getParam1() : tietUrlScheme.getText().toString();
                    String dynamic = tietDynamicParams.getText() == null ? mItem.getParam3() : tietDynamicParams.getText().toString();

                    if (!tietUrlScheme.getText().toString().isEmpty()) {
                        AnywhereEntity ae = new AnywhereEntity(mItem);
                        ae.setParam1(uName);
                        ae.setParam3(dynamic);

                        if (!dynamic.isEmpty()) {
                            DialogManager.showDynamicParamsDialog((AppCompatActivity) mContext, dynamic, new DynamicParamsDialogFragment.OnParamsInputListener() {
                                @Override
                                public void onFinish(String text) {
                                    if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                                        try {
                                            URLSchemeHandler.parse(mItem.getParam1() + text, mContext);
                                        } catch (ActivityNotFoundException e) {
                                            e.printStackTrace();
                                            ToastUtil.makeText(R.string.toast_no_react_url);
                                        }
                                    } else {
                                        CommandUtils.execCmd(String.format(Const.CMD_OPEN_URL_SCHEME_FORMAT, mItem.getParam1()) + text);
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        } else {
                            if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                                try {
                                    URLSchemeHandler.parse(mItem.getParam1(), mContext);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    ToastUtil.makeText(R.string.toast_no_react_url);
                                }
                            } else {
                                CommandUtils.execCmd(TextUtils.getItemCommand(ae));
                            }
                        }
                    }
                }
            });
        }
    }
}

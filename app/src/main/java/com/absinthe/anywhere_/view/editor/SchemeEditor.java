package com.absinthe.anywhere_.view.editor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.fragment.DynamicParamsDialogFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
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

    private TextInputLayout tilAppName, tilUrlScheme;
    private TextInputEditText tietAppName, tietUrlScheme, tietDynamicParams, tietDescription;

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

        tilAppName = container.findViewById(R.id.til_app_name);
        tilUrlScheme = container.findViewById(R.id.til_url_scheme);

        tietAppName = container.findViewById(R.id.tiet_app_name);
        tietUrlScheme = container.findViewById(R.id.tiet_url_scheme);
        tietDynamicParams = container.findViewById(R.id.tiet_dynamic_params);
        tietDescription = container.findViewById(R.id.tiet_description);

        if (tietUrlScheme != null) {
            tietUrlScheme.setText(mItem.getParam1());
        }
        if (tietAppName != null) {
            tietAppName.setText(mItem.getAppName());
        }
        if (tietDescription != null) {
            tietDescription.setText(mItem.getDescription());
        }

        if (tietDynamicParams != null && !TextUtils.isEmpty(mItem.getParam3())) {
            tietDynamicParams.setText(mItem.getParam3());
        }

        Button btnUrlSchemeCommunity = container.findViewById(R.id.btn_url_scheme_community);
        if (btnUrlSchemeCommunity != null) {
            btnUrlSchemeCommunity.setOnClickListener(view -> {
                try {
                    URLSchemeHandler.INSTANCE.parse(URLManager.SHORTCUT_COMMUNITY_PAGE, mContext);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (e instanceof ActivityNotFoundException) {
                        ToastUtil.makeText(R.string.toast_no_react_url);
                    } else if (e instanceof RuntimeException) {
                        ToastUtil.makeText(R.string.toast_runtime_error);
                    }
                }
            });
        }
    }

    @Override
    protected void setDoneButton() {
        if (btnDone != null) {
            btnDone.setOnClickListener(view -> {
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
                                    if (AppUtils.INSTANCE.atLeastNMR1()) {
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
                    ToastUtil.makeText("Error data");
                }
            });
        }
    }

    @Override
    protected void setRunButton() {
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
                                    if (GlobalValues.INSTANCE.getWorkingMode().equals(Const.WORKING_MODE_URL_SCHEME)) {
                                        try {
                                            URLSchemeHandler.INSTANCE.parse(mItem.getParam1() + text, mContext);
                                        } catch (Exception e) {
                                            e.printStackTrace();

                                            if (e instanceof ActivityNotFoundException) {
                                                ToastUtil.makeText(R.string.toast_no_react_url);
                                            } else if (e instanceof RuntimeException) {
                                                ToastUtil.makeText(R.string.toast_runtime_error);
                                            }
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
                            if (GlobalValues.INSTANCE.getWorkingMode().equals(Const.WORKING_MODE_URL_SCHEME)) {
                                try {
                                    URLSchemeHandler.INSTANCE.parse(mItem.getParam1(), mContext);
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    if (e instanceof ActivityNotFoundException) {
                                        ToastUtil.makeText(R.string.toast_no_react_url);
                                    } else if (e instanceof RuntimeException) {
                                        ToastUtil.makeText(R.string.toast_runtime_error);
                                    }
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

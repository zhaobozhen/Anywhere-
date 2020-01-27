package com.absinthe.anywhere_.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import jonathanfinerty.once.Once;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ImageEditor extends Editor<ImageEditor> implements MaterialButtonToggleGroup.OnButtonCheckedListener {

    private TextInputLayout tilUrl;
    private TextInputEditText tietUrl;
    private ImageView ivPreview;

    public ImageEditor(Context context) {
        super(context, Editor.IMAGE);
    }

    @Override
    protected void initView() {
        super.initView();

        MaterialButtonToggleGroup btns = mBottomSheetDialog.findViewById(R.id.toggle_group);
        if (btns != null) {
            btns.addOnButtonCheckedListener(this);
        }
        tilUrl = mBottomSheetDialog.findViewById(R.id.til_url);
        if (tilUrl != null) {
            tilUrl.setEnabled(false);
        }

        ivPreview = mBottomSheetDialog.findViewById(R.id.iv_preview);

        if (ivPreview != null) {
            ivPreview.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    ((BaseActivity) mContext).startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_no_document_app);
                }
            });
        }

        ((BaseActivity) mContext).setDocumentResultListener(uri -> {
            loadImage(uri.toString());
            if (tietUrl != null) {
                tietUrl.setText(uri.toString());
            }
        });

        tietUrl = mBottomSheetDialog.findViewById(R.id.tiet_url);
        if (tietUrl != null) {
            if (isEditMode) {
                tietUrl.setText(mItem.getParam1());
                loadImage(mItem.getParam1());
            }
            tietUrl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isImageUrl(s.toString())) {
                        loadImage(s.toString());
                    }
                }
            });
        }
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_image);
    }

    @Override
    protected void setDoneButton() {
        Button btnEditAnywhereDone = mBottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietUrl != null && tietAppName != null && tietDescription != null) {
                    String appName = tietAppName.getText() == null ? mItem.getAppName() : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                    String url = tietUrl.getText() == null ? "" : tietUrl.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietUrl.getText().toString().isEmpty() && tilUrl != null) {
                        tilUrl.setError(mContext.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietUrl.getText().toString().isEmpty()) {
                        AnywhereEntity ae = AnywhereEntity.Builder();
                        ae.setId(mItem.getId());
                        ae.setAppName(appName);
                        ae.setParam1(url);
                        ae.setDescription(desc);
                        ae.setType(mItem.getType());
                        ae.setCategory(GlobalValues.sCategory);
                        ae.setTimeStamp(mItem.getTimeStamp());
                        ae.setColor(mItem.getColor());
                        if (isEditMode) {
                            if (!appName.equals(mItem.getAppName()) || !url.equals(mItem.getParam1())) {
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

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        switch (checkedId) {
            case R.id.btn_local:
                if (isChecked) {
                    if (!isEditMode) {
                        tietUrl.setText("");
                        ivPreview.setImageDrawable(mContext.getDrawable(R.drawable.ic_image_placeholder));
                    }
                    tilUrl.setEnabled(false);
                    ivPreview.setClickable(true);
                }
                break;
            case R.id.btn_web:
                if (isChecked) {
                    tilUrl.setEnabled(true);
                    ivPreview.setClickable(false);
                }
                break;
            default:
        }
    }

    private void loadImage(String url) {
        if (ivPreview == null) {
            return;
        }

        Glide.with(mContext)
                .load(url)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(UiUtils.d2p(mContext, 5))))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPreview);
    }
}

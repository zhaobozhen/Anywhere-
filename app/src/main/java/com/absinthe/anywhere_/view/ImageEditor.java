package com.absinthe.anywhere_.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ImageEditor extends Editor<ImageEditor> implements MaterialButtonToggleGroup.OnButtonCheckedListener {

    private ViewFlipper mViewFlipper;

    public ImageEditor(Context context) {
        super(context, Editor.IMAGE);
    }

    @Override
    protected void initView() {
        super.initView();

        mViewFlipper = mBottomSheetDialog.findViewById(R.id.vf_container);
        if (mViewFlipper != null) {
            mViewFlipper.setInAnimation(mContext, R.anim.anim_fade_in);
            mViewFlipper.setOutAnimation(mContext, R.anim.anim_fade_out);
        }

        MaterialButtonToggleGroup btns = mBottomSheetDialog.findViewById(R.id.toggle_group);
        if (btns != null) {
            btns.addOnButtonCheckedListener(this);
        }

        ImageView ivPreview = mBottomSheetDialog.findViewById(R.id.iv_preview);
        ((BaseActivity) mContext).setDocumentResultListener(uri -> {
            if (ivPreview != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(uri)
                        .fitCenter()
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(UiUtils.d2p(mContext, 20))))
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                int imageWidth = resource.getWidth();
                                int imageHeight = resource.getHeight();

                                if (imageHeight > UiUtils.d2p(mContext, 250)) {
                                    int height = UiUtils.d2p(mContext, 250);
                                    int width = height * imageWidth / imageHeight;
                                    ViewGroup.LayoutParams para = ivPreview.getLayoutParams();
                                    para.height = height;
                                    para.width = width;
                                }

                                ivPreview.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        });

        MaterialButton btnSelect = mBottomSheetDialog.findViewById(R.id.btn_select);
        if (btnSelect != null) {
            btnSelect.setOnClickListener(v -> {
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
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_image);
    }

    @Override
    protected void setDoneButton() {

    }

    @Override
    protected void setRunButton() {

    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        switch (checkedId) {
            case R.id.btn_local:
                if (mViewFlipper != null && isChecked) {
                    mViewFlipper.setDisplayedChild(0);
                }
                break;
            case R.id.btn_net:
                if (mViewFlipper != null && isChecked) {
                    mViewFlipper.setDisplayedChild(1);
                }
                break;
            default:
        }
    }
}

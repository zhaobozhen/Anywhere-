package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.ImageDialogBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private AnywhereEntity mItem;
    private ImageDialogBuilder mBuilder;

    private String mUri;
    private int mWidth, mHeight;

    public ImageDialogFragment(AnywhereEntity ae) {
        this(ae, null);
    }

    public ImageDialogFragment(AnywhereEntity ae, OnDismissListener listener) {
        mItem = ae;
        mUri = ae.getParam1();

        int[] imageParam = getImageParams(ae.getParam2());
        mWidth = imageParam[0];
        mHeight = imageParam[1];
        setWrapOnDismissListener(listener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder dialog = new AnywhereDialogBuilder(mContext);
        mBuilder = new ImageDialogBuilder(mContext);
        if (mWidth != 0 && mHeight != 0) {
            mBuilder.getRoot().setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
        }
        dialog.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_shape));

        return dialog.setView(mBuilder.getRoot()).create();
    }

    @Override
    public void onStart() {
        super.onStart();

        initView();
    }

    private int[] getImageParams(String param) {
        String[] params = param.split(",");
        if (params.length != 2) {
            return new int[] {0, 0};
        }

        return new int[] {Integer.valueOf(params[0]), Integer.valueOf(params[1])};
    }

    private void initView() {
        Glide.with(mContext)
                .asBitmap()
                .load(mUri)
                .placeholder(new ColorDrawable(Color.TRANSPARENT))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (mWidth != 0 && mHeight != 0) {
                            mBuilder.getRoot().setLayoutParams(new FrameLayout.LayoutParams(mWidth, mHeight));
                            mBuilder.image.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
                        } else {
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();

                            int width = mBuilder.getRoot().getWidth();
                            int height = width * imageHeight / imageWidth;

                            mBuilder.getRoot().setLayoutParams(new FrameLayout.LayoutParams(width, height));
                            mBuilder.image.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                            mItem.setParam2(width + "," + height);
                            AnywhereApplication.sRepository.update(mItem);
                        }

                        try {
                            mBuilder.image.setImageBitmap(resource);
                        } catch (RuntimeException e) {
                            ToastUtil.makeText("Image too large :(");
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}

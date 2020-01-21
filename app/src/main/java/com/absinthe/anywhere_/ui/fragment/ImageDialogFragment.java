package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.ImageDialogBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private String mUri;
    private ImageDialogBuilder mBuilder;

    public ImageDialogFragment(String uri) {
        mUri = uri;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        initView();

        return builder.setView(mBuilder.getRoot())
                .create();
    }

    private void initView() {
        mBuilder = new ImageDialogBuilder(mContext);

        Glide.with(mContext).asBitmap()
                .load(mUri)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(mBuilder.image);
    }
}

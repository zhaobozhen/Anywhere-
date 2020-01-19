package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.ImageDialogBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ImageDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private ImageDialogBuilder mBuilder;

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

        Glide.with(mContext)
                .load(GlobalValues.sBackgroundUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mBuilder.image);
    }
}

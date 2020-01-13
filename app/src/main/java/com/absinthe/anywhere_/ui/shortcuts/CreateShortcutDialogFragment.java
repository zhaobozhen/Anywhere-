package com.absinthe.anywhere_.ui.shortcuts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.CreateShortcutDialogBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import static android.app.Activity.RESULT_OK;

public class CreateShortcutDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private CreateShortcutDialogBuilder mBuilder;

    private AnywhereEntity mEntity;
    private Drawable mIcon;
    private String mName;

    public CreateShortcutDialogFragment(AnywhereEntity ae) {
        mEntity = ae;
        mName = ae.getAppName();
        mIcon = UiUtils.getAppIconByPackageName(AnywhereApplication.sContext, ae.getParam1());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new CreateShortcutDialogBuilder(mContext);
        initView();

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_set_icon_and_name_title)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) ->
                        ShortcutsUtils.addPinnedShortcut(mEntity,
                                mBuilder.ivIcon.getDrawable(), mBuilder.etName.getText().toString()))
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create();
    }

    private void initView() {
        mBuilder.ivIcon.setImageDrawable(mIcon);
        mBuilder.etName.setText(mName);

        mBuilder.ivIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri iconUri = data.getData();
                    if (iconUri != null) {
                        Glide.with(this)
                                .load(iconUri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(mBuilder.ivIcon);
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

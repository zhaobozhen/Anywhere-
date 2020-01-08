package com.absinthe.anywhere_.ui.shortcuts;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewbuilder.entity.CreateShortcutDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CreateShortcutDialogFragment extends DialogFragment {
    private Context mContext;
    private CreateShortcutDialogBuilder mBuilder;

    private Drawable mIcon;
    private String mName;

    public CreateShortcutDialogFragment(String name, Drawable icon) {
        mName = name;
        mIcon = icon;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog);
        mBuilder = new CreateShortcutDialogBuilder(mContext);
        initView();

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_title_choose_icon_pack)
                .create();
    }

    private void initView() {
        mBuilder.ivIcon.setImageDrawable(mIcon);
        mBuilder.etName.setText(mName);
    }
}

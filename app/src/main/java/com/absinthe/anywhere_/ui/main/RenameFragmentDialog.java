package com.absinthe.anywhere_.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.page.PageTitleProvider;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.RenameDialogBuilder;

public class RenameFragmentDialog extends AnywhereDialogFragment {
    private Context mContext;
    private RenameDialogBuilder mBuilder;
    private String mTitle;

    public RenameFragmentDialog(String title) {
        mTitle = title;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new RenameDialogBuilder(mContext);
        mBuilder.etName.setText(mTitle);

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_rename_title)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) ->
                        PageTitleProvider.renameTitle(mTitle, mBuilder.etName.getText().toString()))
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create();
    }

    public String getText() {
        return mBuilder.etName.getText().toString();
    }
}

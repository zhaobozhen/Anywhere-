package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;

public class IceBoxGrantDialogFragment extends AnywhereDialogFragment {
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AnywhereDialogBuilder(mContext)
                .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$ManageApplicationsActivity"));
                    mContext.startActivity(intent);
                })
                .create();
    }
}

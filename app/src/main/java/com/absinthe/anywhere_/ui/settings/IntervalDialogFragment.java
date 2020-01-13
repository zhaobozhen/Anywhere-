package com.absinthe.anywhere_.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.IntervalDialogBuilder;

public class IntervalDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private IntervalDialogBuilder mBuilder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new IntervalDialogBuilder(mContext);
        mBuilder.slider.setValue(GlobalValues.sDumpInterval / 1000);

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_set_interval_title)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) -> {
                    int interval = (int) mBuilder.slider.getValue() * 1000;
                    GlobalValues.setsDumpInterval(interval);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create();
    }
}

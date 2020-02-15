package com.absinthe.anywhere_.ui.gift;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.GiftPurchaseDialogBuilder;

public class GiftPriceDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private GiftPurchaseDialogBuilder mBuilder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new GiftPurchaseDialogBuilder(mContext);

        return builder.setView(mBuilder.getRoot())
                .setTitle("解锁“艾薇的馈赠”")
                .create();
    }
}

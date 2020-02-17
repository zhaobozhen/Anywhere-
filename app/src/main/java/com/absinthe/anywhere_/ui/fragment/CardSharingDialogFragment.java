package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.CardSharingBuilder;

public class CardSharingDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private String mText;

    public CardSharingDialogFragment(String text) {
        mText = text;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        CardSharingBuilder cardSharingBuilder = new CardSharingBuilder(mContext, mText);

        return builder.setView(cardSharingBuilder.getRoot())
                .setTitle(R.string.menu_share_card)
                .setPositiveButton(R.string.dialog_copy, (dialog, which) -> {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", mText);
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.makeText(R.string.toast_copied);
                    }
                })
                .create();
    }
}

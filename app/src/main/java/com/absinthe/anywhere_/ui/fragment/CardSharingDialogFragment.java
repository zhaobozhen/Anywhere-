package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.QRCodeUtil;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;

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

        int size = UiUtils.d2p(mContext, 250);

        ImageView ivQrCode = new ImageView(mContext);
        ivQrCode.setLayoutParams(new LinearLayout.LayoutParams(size, size));

        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(mText, size, size);
        if (bitmap != null) {
            ivQrCode.setImageBitmap(bitmap);
        }

        return builder.setView(ivQrCode)
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

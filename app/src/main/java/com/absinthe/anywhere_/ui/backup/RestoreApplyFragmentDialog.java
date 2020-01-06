package com.absinthe.anywhere_.ui.backup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class RestoreApplyFragmentDialog extends DialogFragment {
    private Context mContext;
    private TextInputEditText mEditText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_restore_apply, container, false);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog);
        LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
        @SuppressLint("InflateParams")
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_restore_apply, null, false);

        mEditText = inflate.findViewById(R.id.tiet_paste);
        DialogInterface.OnClickListener listener = (d, which) -> {
            String encrypted1 = getText();
            if (encrypted1 != null) {
                String content1 = CipherUtils.decrypt(encrypted1);
                Gson gson = new Gson();
                List<AnywhereEntity> list = gson.fromJson(content1,
                        new TypeToken<List<AnywhereEntity>>() {
                        }.getType());

                if (list != null) {
                    for (AnywhereEntity ae : list) {
                        MainActivity.getInstance().getViewModel().insert(ae);
                    }
                    ToastUtil.makeText(getString(R.string.toast_restore_success));
                }
            }
        };

        return builder.setView(inflate)
                .setTitle(R.string.settings_backup_apply_title)
                .setPositiveButton(R.string.btn_apply, listener)
                .create();
    }

    public String getText() {
        if (mEditText != null && mEditText.getText() != null) {
            return mEditText.getText().toString();
        } else {
            return null;
        }
    }
}

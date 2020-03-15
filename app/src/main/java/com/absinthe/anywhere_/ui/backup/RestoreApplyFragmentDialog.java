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

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.ListUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import timber.log.Timber;

import static com.absinthe.anywhere_.ui.backup.BackupActivity.INSERT_CORRECT;

public class RestoreApplyFragmentDialog extends AnywhereDialogFragment {
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
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
        @SuppressLint("InflateParams")
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_restore_apply, null, false);

        mEditText = inflate.findViewById(R.id.tiet_paste);
        DialogInterface.OnClickListener listener = (d, which) -> {
            String encrypted1 = getText();
            if (encrypted1 != null) {
                String content1 = CipherUtils.decrypt(encrypted1);
                Timber.d(content1);
                Gson gson = new Gson();

                try {
                    List<AnywhereEntity> list = gson.fromJson(content1,
                            new TypeToken<List<AnywhereEntity>>() {}.getType());

                    if (list != null) {
                        INSERT_CORRECT = true;
                        for (AnywhereEntity ae : list) {
                            if (!INSERT_CORRECT) {
                                ToastUtil.makeText(R.string.toast_backup_file_error);
                                break;
                            }
                            List<PageEntity> pageEntityList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
                            if (pageEntityList != null) {
                                if (ListUtils.getPageEntityByTitle(ae.getCategory()) == null) {
                                    PageEntity pe = PageEntity.Builder();
                                    pe.setTitle(ae.getCategory());
                                    pe.setPriority(pageEntityList.size() + 1);
                                    pe.setType(AnywhereType.CARD_PAGE);
                                    AnywhereApplication.sRepository.insertPage(pe);
                                }
                            }
                            AnywhereApplication.sRepository.insert(ae);
                        }
                        if (INSERT_CORRECT) {
                            ToastUtil.makeText(getString(R.string.toast_restore_success));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_backup_file_error);
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

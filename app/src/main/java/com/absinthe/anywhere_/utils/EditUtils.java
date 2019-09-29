package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.SelectableCardsAdapter;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class EditUtils {
    private static final String TAG = "EditUtils";
    private static BottomSheetDialog bottomSheetDialog = null;

    public static void editAnywhere(@NonNull Activity activity, String packageName, String className, int classNameType, String appName) {
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_content);
        bottomSheetDialog.setDismissWithAnimation(true);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietPackageName = bottomSheetDialog.findViewById(R.id.tiet_package_name);
        TextInputEditText tietClassName = bottomSheetDialog.findViewById(R.id.tiet_class_name);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
//            tietAppName.setText(String.format("%s - Anywhere-01", appName));
            tietAppName.setText(appName);
        }

        if (tietPackageName != null) {
            tietPackageName.setText(packageName);
        }

        if (tietClassName != null) {
            tietClassName.setText(className);
        }

        if (tietDescription != null) {
            tietDescription.setText(null);
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null) {
                    String pName = tietPackageName.getText() == null ? packageName : tietPackageName.getText().toString();
                    String cName = tietClassName.getText() == null ? className : tietClassName.getText().toString();
                    String aName = tietAppName.getText() == null ? appName : tietAppName.getText().toString();
                    String description = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                    Log.d(TAG, "description == " + description);

                    MainFragment.getViewModelInstance().insert(new AnywhereEntity(pName, cName, classNameType, aName, description));
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(activity, "error data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        bottomSheetDialog.show();
    }

    public static void editAnywhere(@NonNull Activity activity, SelectableCardsAdapter adapter, AnywhereEntity item, int position, boolean withDeleteButton) {
        editAnywhere(activity, item.getPackageName(), item.getClassName(), item.getClassNameType(), item.getAppName());

        ImageButton ibDelete = bottomSheetDialog.findViewById(R.id.ib_delete_anywhere);
        if (ibDelete != null) {
            if (withDeleteButton) {
                ibDelete.setVisibility(View.VISIBLE);
            } else {
                ibDelete.setVisibility(View.GONE);
            }
            ibDelete.setOnClickListener(view -> deleteAnywhereActivity(activity, item, adapter, position));
        }

    }

    private static void deleteAnywhereActivity(Context context, AnywhereEntity ae, SelectableCardsAdapter adapter, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    bottomSheetDialog.dismiss();
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> { })
                .show();
    }
}

package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    @SuppressLint("StaticFieldLeak")
    private static BottomSheetDialog bottomSheetDialog = null;

    public static void editAnywhere(@NonNull Activity activity, String packageName, String className, int classNameType, String appName, String description) {
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
            tietDescription.setText(description);
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null) {
                    String pName = tietPackageName.getText() == null ? packageName : tietPackageName.getText().toString();
                    String cName = tietClassName.getText() == null ? className : tietClassName.getText().toString();
                    String aName = tietAppName.getText() == null ? appName : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();
                    Log.d(TAG, "description == " + desc);

                    MainFragment.getViewModelInstance().insert(new AnywhereEntity(pName, cName, classNameType, "", aName, desc, System.currentTimeMillis() + ""));
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(activity, "error data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        bottomSheetDialog.show();
    }

    public static void editAnywhere(@NonNull Activity activity, SelectableCardsAdapter adapter, AnywhereEntity item, int position, boolean withDeleteButton) {
        editAnywhere(activity, item.getPackageName(), item.getClassName(), item.getClassNameType(), item.getAppName(), item.getCustomTexture());
        adapter.notifyItemChanged(position);

        ImageButton ibDelete = bottomSheetDialog.findViewById(R.id.ib_delete_anywhere);
        if (ibDelete != null) {
            if (withDeleteButton) {
                ibDelete.setVisibility(View.VISIBLE);
            } else {
                ibDelete.setVisibility(View.GONE);
            }
            ibDelete.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                deleteAnywhereActivity(activity, item, adapter, position);
            });
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
                        (dialogInterface, i) -> bottomSheetDialog.show())
                .show();
    }

    public static void editUrlScheme(Activity activity) {
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_url_scheme);
        bottomSheetDialog.setDismissWithAnimation(true);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietUrlScheme = bottomSheetDialog.findViewById(R.id.tiet_url_scheme);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText("URL Scheme");
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietUrlScheme != null && tietAppName != null && tietDescription != null) {
                    String uScheme = tietUrlScheme.getText() == null ? "" : tietUrlScheme.getText().toString();
                    String aName = tietAppName.getText() == null  ? "URL Scheme" : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    MainFragment.getViewModelInstance().insert(new AnywhereEntity("pName", "cName", ConstUtil.URL_SCHEME_TYPE, uScheme, aName, desc, System.currentTimeMillis() + ""));
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(activity, "error data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnUrlSchemeCommunity = bottomSheetDialog.findViewById(R.id.btn_url_scheme_community);
        if (btnUrlSchemeCommunity != null) {
            btnUrlSchemeCommunity.setOnClickListener(view -> {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://sharecuts.cn/apps"));
                activity.startActivity(intent);
            });
        }

        bottomSheetDialog.show();
    }

    public static void editUrlScheme(@NonNull Activity activity, SelectableCardsAdapter adapter, AnywhereEntity item, int position, boolean withDeleteButton) {
        editUrlScheme(activity);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietUrlScheme = bottomSheetDialog.findViewById(R.id.tiet_url_scheme);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(item.getAppName());
        }

        if (tietUrlScheme != null) {
            tietUrlScheme.setText(item.getUrlScheme());
        }

        if (tietDescription != null) {
            tietDescription.setText(item.getCustomTexture());
        }

        adapter.notifyItemChanged(position);

        ImageButton ibDelete = bottomSheetDialog.findViewById(R.id.ib_delete_anywhere);
        if (ibDelete != null) {
            if (withDeleteButton) {
                ibDelete.setVisibility(View.VISIBLE);
            } else {
                ibDelete.setVisibility(View.GONE);
            }
            ibDelete.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                deleteAnywhereActivity(activity, item, adapter, position);
            });
        }

    }
}

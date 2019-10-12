package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.SelectableCardsAdapter;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class EditUtils {
    private static final String TAG = "EditUtils";

    @SuppressLint("StaticFieldLeak")
    private static BottomSheetDialog bottomSheetDialog = null;

    public static void editAnywhere(@NonNull Activity activity, AnywhereEntity item, boolean isUpdate) {
        View contentView = View.inflate(activity, R.layout.bottom_sheet_dialog_content, null);
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(contentView);
        bottomSheetDialog.setDismissWithAnimation(true);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextInputLayout tilAppName = bottomSheetDialog.findViewById(R.id.til_app_name);
        TextInputLayout tilPackageName = bottomSheetDialog.findViewById(R.id.til_package_name);
        TextInputLayout tilClassName = bottomSheetDialog.findViewById(R.id.til_class_name);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietPackageName = bottomSheetDialog.findViewById(R.id.tiet_package_name);
        TextInputEditText tietClassName = bottomSheetDialog.findViewById(R.id.tiet_class_name);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(item.getAppName());
        }

        if (tietPackageName != null) {
            tietPackageName.setText(item.getParam1());
        }

        if (tietClassName != null) {
            tietClassName.setText(item.getParam2());
        }

        if (tietDescription != null) {
            tietDescription.setText(item.getDescription());
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietPackageName != null && tietClassName != null && tietAppName != null && tietDescription != null) {
                    String pName = tietPackageName.getText() == null ? item.getParam1() : tietPackageName.getText().toString();
                    String cName = tietClassName.getText() == null ? item.getParam2() : tietClassName.getText().toString();
                    String aName = tietAppName.getText() == null ? item.getAppName() : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                            tilAppName.setError(activity.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietPackageName.getText().toString().isEmpty() && tilPackageName != null) {
                        tilPackageName.setError(activity.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietClassName.getText().toString().isEmpty() && tilClassName != null) {
                        tilClassName.setError(activity.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietPackageName.getText().toString().isEmpty()
                            && !tietClassName.getText().toString().isEmpty()) {
                        AnywhereEntity ae = new AnywhereEntity(aName, pName, cName, item.getParam3(), desc, item.getType(), item.getTimeStamp());
                        if (isUpdate) {
                            if (!aName.equals(item.getAppName()) || !pName.equals(item.getParam1())) {
                                MainFragment.getViewModelInstance().insert(ae);
                                MainFragment.getViewModelInstance().delete(item);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    ShortcutsUtil.removeShortcut(item);
                                    ShortcutsUtil.addShortcut(ae);
                                }
                            } else if (!cName.equals(item.getParam2())) {
                                MainFragment.getViewModelInstance().update(ae);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    ShortcutsUtil.removeShortcut(item);
                                    ShortcutsUtil.addShortcut(ae);
                                }
                            } else {
                                MainFragment.getViewModelInstance().update(ae);
                            }
                        } else {
                            if (hasSameAppName(aName, item.getParam1())) {
                                bottomSheetDialog.dismiss();
                                new MaterialAlertDialogBuilder(activity)
                                        .setMessage(R.string.dialog_message_same_app_name)
                                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                                            MainFragment.getViewModelInstance().insert(ae);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                                ShortcutsUtil.removeShortcut(Objects.requireNonNull(hasSameAppNameEntity(aName, item.getParam1())));
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> bottomSheetDialog.show())
                                        .show();
                            } else {
                                MainFragment.getViewModelInstance().insert(ae);
                            }
                        }
                        bottomSheetDialog.dismiss();
                    }

                } else {
                    ToastUtil.makeText("error data.");
                }
            });
        }

        bottomSheetDialog.show();
    }

    public static void editAnywhere(@NonNull Activity activity, SelectableCardsAdapter adapter, AnywhereEntity item, int position, boolean withDeleteButton) {
        editAnywhere(activity, item, true);

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
                adapter.notifyItemChanged(position);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ImageButton ibAddShortcut = bottomSheetDialog.findViewById(R.id.ib_add_shortcut);
            if (ibAddShortcut != null) {
                if (withDeleteButton) {
                    ibAddShortcut.setVisibility(View.VISIBLE);
                    if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                        ibAddShortcut.setBackgroundResource(R.drawable.ic_added_shortcut);
                    }
                } else {
                    ibAddShortcut.setVisibility(View.GONE);
                }
                ibAddShortcut.setOnClickListener(view -> {
                    bottomSheetDialog.dismiss();
                    if (item.getShortcutType() != AnywhereType.SHORTCUTS) {
                        addShortcut(activity, item);
                    } else {
                        removeShortcut(activity, item);
                    }
                    adapter.notifyItemChanged(position);
                });
            }
        }

    }

    public static void editUrlScheme(@NonNull Activity activity, AnywhereEntity item, boolean isUpdate) {
        View contentView = View.inflate(activity, R.layout.bottom_sheet_dialog_url_scheme, null);
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(contentView);
        bottomSheetDialog.setDismissWithAnimation(true);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextInputLayout tilAppName = bottomSheetDialog.findViewById(R.id.til_app_name);
        TextInputLayout tilUrlScheme = bottomSheetDialog.findViewById(R.id.til_url_scheme);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietUrlScheme = bottomSheetDialog.findViewById(R.id.tiet_url_scheme);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(R.string.bsd_new_url_scheme_name);
        }

        Button btnEditAnywhereDone = bottomSheetDialog.findViewById(R.id.btn_edit_anywhere_done);
        if (btnEditAnywhereDone != null) {
            btnEditAnywhereDone.setOnClickListener(view -> {
                if (tietUrlScheme != null && tietAppName != null && tietDescription != null) {
                    String uScheme = tietUrlScheme.getText() == null ? "" : tietUrlScheme.getText().toString();
                    String aName = tietAppName.getText() == null  ? AnywhereApplication.sContext.getString(R.string.bsd_new_url_scheme_name) : tietAppName.getText().toString();
                    String desc = tietDescription.getText() == null ? "" : tietDescription.getText().toString();

                    if (tietAppName.getText().toString().isEmpty() && tilAppName != null) {
                        tilAppName.setError(activity.getString(R.string.bsd_error_should_not_empty));
                    }
                    if (tietUrlScheme.getText().toString().isEmpty() && tilUrlScheme != null) {
                        tilUrlScheme.setError(activity.getString(R.string.bsd_error_should_not_empty));
                    }

                    if (!tietAppName.getText().toString().isEmpty()
                            && !tietUrlScheme.getText().toString().isEmpty()) {
                        AnywhereEntity ae = new AnywhereEntity(aName, uScheme, null, null, desc, item.getType(), item.getTimeStamp());

                        if (isUpdate) {
                            if (!aName.equals(item.getAppName()) || !uScheme.equals(item.getParam1())) {
                                MainFragment.getViewModelInstance().insert(ae);
                                MainFragment.getViewModelInstance().delete(item);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    ShortcutsUtil.removeShortcut(item);
                                    ShortcutsUtil.addShortcut(ae);
                                }
                            } else {
                                MainFragment.getViewModelInstance().update(ae);
                            }
                        } else {
                            if (hasSameAppName(aName, uScheme)) {
                                bottomSheetDialog.dismiss();
                                new MaterialAlertDialogBuilder(activity)
                                        .setMessage(R.string.dialog_message_same_app_name)
                                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                                            MainFragment.getViewModelInstance().insert(ae);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                                ShortcutsUtil.removeShortcut(Objects.requireNonNull(hasSameAppNameEntity(aName, item.getParam1())));
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> bottomSheetDialog.show())
                                        .show();
                            } else {
                                MainFragment.getViewModelInstance().insert(ae);
                            }
                        }
                        bottomSheetDialog.dismiss();
                    }
                } else {
                    ToastUtil.makeText("error data.");
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
        editUrlScheme(activity, item, true);

        TextInputEditText tietAppName = bottomSheetDialog.findViewById(R.id.tiet_app_name);
        TextInputEditText tietUrlScheme = bottomSheetDialog.findViewById(R.id.tiet_url_scheme);
        TextInputEditText tietDescription = bottomSheetDialog.findViewById(R.id.tiet_description);

        if (tietAppName != null) {
            tietAppName.setText(item.getAppName());
        }

        if (tietUrlScheme != null) {
            tietUrlScheme.setText(item.getParam1());
        }

        if (tietDescription != null) {
            tietDescription.setText(item.getDescription());
        }

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
                adapter.notifyItemChanged(position);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ImageButton ibAddShortcut = bottomSheetDialog.findViewById(R.id.ib_add_shortcut);
            if (ibAddShortcut != null) {
                if (withDeleteButton) {
                    ibAddShortcut.setVisibility(View.VISIBLE);
                    if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                        ibAddShortcut.setBackgroundResource(R.drawable.ic_added_shortcut);
                    }
                } else {
                    ibAddShortcut.setVisibility(View.GONE);
                }
                ibAddShortcut.setOnClickListener(view -> {
                    bottomSheetDialog.dismiss();
                    if (item.getShortcutType() != AnywhereType.SHORTCUTS) {
                        addShortcut(activity, item);
                    } else {
                        removeShortcut(activity, item);
                    }
                    adapter.notifyItemChanged(position);
                });
            }
        }
    }

    private static boolean hasSameAppName(String name, String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (name.equals(ae.getAppName()) && param1.equals(ae.getParam1())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static AnywhereEntity hasSameAppNameEntity(String name, String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (name.equals(ae.getAppName()) && param1.equals(ae.getParam1())) {
                    return ae;
                }
            }
        }
        return null;
    }

    private static void deleteAnywhereActivity(Context context, AnywhereEntity ae, SelectableCardsAdapter adapter, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtil.removeShortcut(ae);
                    }
                    bottomSheetDialog.dismiss();
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> bottomSheetDialog.show())
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private static void addShortcut(Context context, AnywhereEntity ae) {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtil.addShortcut(ae);

            bottomSheetDialog.dismiss();
        };

        MaterialAlertDialogBuilder addDialog =  new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_add_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, (dialogInterface, i) -> bottomSheetDialog.show());

        MaterialAlertDialogBuilder cantAddDialog =  new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> bottomSheetDialog.show());

        if (ShortcutsUtil.getInstance().getDynamicShortcuts().size() < 3) {
            addDialog.show();
        } else {
            cantAddDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)

    private static void removeShortcut(Context context, AnywhereEntity ae) {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
            ShortcutsUtil.removeShortcut(ae);

            bottomSheetDialog.dismiss();
        };

        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(Html.fromHtml(context.getString(R.string.dialog_remove_shortcut_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> bottomSheetDialog.show())
                .show();
    }
}

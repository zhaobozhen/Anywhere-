package com.absinthe.anywhere_.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.VibratorUtil;
import com.absinthe.anywhere_.view.Editor;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements ItemTouchCallBack.OnItemTouchListener {
    public static final int ADAPTER_MODE_NORMAL = 0;
    public static final int ADAPTER_MODE_SORT = 1;
    public static final int ADAPTER_MODE_SELECT = 2;

    protected Context mContext;
    private Editor mEditor;
    private List<Integer> selectedIndex;
    List<AnywhereEntity> items;
    int mode;

    BaseAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.selectedIndex = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    public void setItems(List<AnywhereEntity> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public List<Integer> getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AnywhereEntity item = items.get(position);

        int type = item.getAnywhereType();

        holder.itemView.setOnClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {
                openAnywhereActivity(item);
                notifyItemChanged(position);
            } else if (mode == ADAPTER_MODE_SELECT) {
                if (selectedIndex.contains(position)) {
                    holder.itemView.setScaleX(1.0f);
                    holder.itemView.setScaleY(1.0f);
                    selectedIndex.remove((Integer) position);
                } else {
                    holder.itemView.setScaleX(0.9f);
                    holder.itemView.setScaleY(0.9f);
                    selectedIndex.add(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {
                VibratorUtil.vibrate(mContext, VibratorUtil.DEFAULT);

                switch (type) {
                    case AnywhereType.URL_SCHEME:
                        openEditor(item, Editor.URL_SCHEME, position);
                        break;
                    case AnywhereType.ACTIVITY:
                        openEditor(item, Editor.ANYWHERE, position);
                        break;
                    case AnywhereType.MINI_PROGRAM:
                        break;
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        String cmd = TextUtils.getItemCommand(item);
        if (!cmd.isEmpty()) {
            if (AppUtils.isAppFrozen(mContext, item)) {
                if (ContextCompat.checkSelfPermission(AnywhereApplication.sContext, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtil.isMIUI()) {
                        new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                                .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                                .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, i) -> {
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    intent.setComponent(new ComponentName("com.android.settings",
                                            "com.android.settings.Settings$ManageApplicationsActivity"));
                                    mContext.startActivity(intent);
                                })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
                    }
                } else {
                    PermissionUtil.unfreezeApp(mContext, item.getParam1(), () ->
                            MainFragment.getViewModelInstance().getCommand().setValue(cmd));
                }
            } else {
                MainFragment.getViewModelInstance().getCommand().setValue(cmd);
            }
        }
    }

    private void openEditor(AnywhereEntity item, int type, int position) {
        Editor.OnEditorListener listener = new Editor.OnEditorListener() {
            @Override
            public void onDelete() {
                deleteAnywhereActivity(mEditor, item, position);
            }

//            @Override
//            public void onChange() {
//                notifyItemChanged(position);
//            }
        };

        mEditor = new Editor(MainActivity.getInstance(), type)
                .item(item)
                .isEditorMode(true)
                .isShortcut(item.getShortcutType() == AnywhereType.SHORTCUTS)
                .setOnEditorListener(listener)
                .build();

        mEditor.show();
    }

    private void deleteAnywhereActivity(Editor editor, AnywhereEntity ae, int position) {
        new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(mContext.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtil.removeShortcut(ae);
                    }
                    editor.dismiss();
                    notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> editor.show())
                .show();
    }

    public void updateSortedList() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            for (int iter = 0; iter < items.size(); iter++) {
                AnywhereEntity item = items.get(iter);
                AnywhereEntity ae = new AnywhereEntity(item.getId(), item.getAppName(), item.getParam1(),
                        item.getParam2(), item.getParam3(), item.getDescription(), item.getType(),
                        startTime - iter * 100 + "");
                MainFragment.getViewModelInstance().update(ae);
            }
        }).start();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {

    }

    public void deleteSelect() {
        List<AnywhereEntity> list = new ArrayList<>();
        for (int index : selectedIndex) {
            LogUtil.d(this.getClass(), "index=",index);
            list.add(items.get(index));
        }
        for (AnywhereEntity ae : list) {
            int index = items.indexOf(ae);
            items.remove(index);
            MainFragment.getViewModelInstance().delete(ae);
            notifyItemRemoved(index);
        }
        selectedIndex.clear();
    }

}

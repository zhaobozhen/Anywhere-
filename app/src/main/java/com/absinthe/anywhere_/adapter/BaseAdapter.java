package com.absinthe.anywhere_.adapter;//打包 康姆点艾伯森斯点安妮薇儿下划线点鹅带坡特儿

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.model.QREntity;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.Editor;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>//公共 类 基于鹅带坡特儿《威欸吃 扩展 回收者浏览了点景观持有人》扩展 回收者浏览了点鹅带坡特儿
        implements ItemTouchCallBack.OnItemTouchListener {
    public static final int ADAPTER_MODE_NORMAL = 0;//公共 静态 最终 整型 鹅带坡特儿下划线模式下划线普通的 等于 零
    public static final int ADAPTER_MODE_SORT = 1;
    public static final int ADAPTER_MODE_SELECT = 2;

    protected Context mContext;
    private List<Integer> selectedIndex;
    List<AnywhereEntity> items;
    Editor mEditor;
    int mode;

    BaseAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.selectedIndex = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    public void setItems(List<AnywhereEntity> items) {
        this.items.addAll(items);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void updateItems(List<AnywhereEntity> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffListCallback(this.items, items));
        this.items.clear();
        this.items.addAll(items);
        diffResult.dispatchUpdatesTo(this);
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
                if (AppUtils.isAppFrozen(mContext, item)) {
                    openAnywhereActivity(item);
                    notifyItemChanged(position);
                } else {
                    openAnywhereActivity(item);
                }
            } else if (mode == ADAPTER_MODE_SELECT) {
                if (selectedIndex.contains(position)) {
                    holder.itemView.setScaleX(1.0f);
                    holder.itemView.setScaleY(1.0f);
                    ((MaterialCardView)holder.itemView).setChecked(false);
                    selectedIndex.remove((Integer) position);
                } else {
                    holder.itemView.setScaleX(0.9f);
                    holder.itemView.setScaleY(0.9f);
                    ((MaterialCardView)holder.itemView).setChecked(true);
                    selectedIndex.add(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {

                holder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                switch (type) {
                    case AnywhereType.URL_SCHEME:
                        openEditor(item, Editor.URL_SCHEME, position);
                        break;
                    case AnywhereType.ACTIVITY:
                        openEditor(item, Editor.ANYWHERE, position);
                        break;
                    case AnywhereType.MINI_PROGRAM:
                        break;
                    case AnywhereType.QR_CODE:
                        openEditor(item, Editor.QR_CODE, position);
                        break;
                }
                return true;
            }
            return false;
        });

        if (!selectedIndex.contains(position)) {
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
            ((MaterialCardView)holder.itemView).setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        //Todo Will delete in future version
        if (item.getAnywhereType() == AnywhereType.URL_SCHEME) {
            if (android.text.TextUtils.isEmpty(item.getParam2())) {
                AnywhereEntity ae = new AnywhereEntity(item.getId(),
                        item.getAppName(),
                        item.getParam1(),
                        UiUtils.getPkgNameByUrl(mContext, item.getParam1()),
                        item.getParam3(),
                        item.getDescription(),
                        item.getType(),
                        item.getTimeStamp());
                MainFragment.getViewModelInstance().update(ae);
            }
        }

        if (item.getAnywhereType() != AnywhereType.QR_CODE) {
            String cmd = TextUtils.getItemCommand(item);
            if (!cmd.isEmpty()) {
                if (AppUtils.isAppFrozen(mContext, item)) {
                    if (ContextCompat.checkSelfPermission(AnywhereApplication.sContext, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                        if (PermissionUtil.isMIUI()) {
                            new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                                    .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                                    .setPositiveButton(R.string.dialog_delete_positive_button, null)
                                    .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, i) -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setComponent(new ComponentName("com.android.settings",
                                                "com.android.settings.Settings$ManageApplicationsActivity"));
                                        mContext.startActivity(intent);
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
                        }
                    } else {
                        final OnAppUnfreezeListener onAppUnfreezeListener = () ->
                                MainFragment.getViewModelInstance().getCommand().setValue(cmd);
                        if (item.getAnywhereType() == AnywhereType.URL_SCHEME) {
                            PermissionUtil.unfreezeApp(mContext, item.getParam2(), onAppUnfreezeListener);
                        } else {
                            PermissionUtil.unfreezeApp(mContext, item.getParam1(), onAppUnfreezeListener);
                        }
                    }
                } else {
                    MainFragment.getViewModelInstance().getCommand().setValue(cmd);
                }
            }
        } else {
            QREntity entity = QRCollection.Singleton.INSTANCE.getInstance().getQREntity(item.getParam2());
            if (entity != null) {
                entity.launch();
            }
        }
    }

    void openEditor(AnywhereEntity item, int type, int position) {
        Editor.OnEditorListener listener = () -> deleteAnywhereActivity(mEditor, item, position);

        mEditor = new Editor(mContext, type)
                .item(item)
                .isEditorMode(true)
                .isShortcut(item.getShortcutType() == AnywhereType.SHORTCUTS)
                .isExported(item.getExportedType() == AnywhereType.EXPORTED)
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
            MainFragment.getViewModelInstance().refreshLock = true;
            long startTime = System.currentTimeMillis();

            for (int iter = 0; iter < items.size(); iter++) {
                AnywhereEntity item = items.get(iter);
                AnywhereEntity ae = new AnywhereEntity(item.getId(), item.getAppName(), item.getParam1(),
                        item.getParam2(), item.getParam3(), item.getDescription(), item.getType(),
                        startTime - iter * 100 + "");
                MainFragment.getViewModelInstance().update(ae);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainFragment.getViewModelInstance().refreshLock = false;
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
        if (selectedIndex.size() == 0) {
            return;
        }

        List<AnywhereEntity> list = new ArrayList<>();
        for (int index : selectedIndex) {
            if (index < items.size()) {
                list.add(items.get(index));
            }
        }
        for (AnywhereEntity ae : list) {
            int index = items.indexOf(ae);
            items.remove(index);
            MainFragment.getViewModelInstance().delete(ae);
            notifyItemRemoved(index);
        }
        clearSelect();
    }

    public void clearSelect() {
        selectedIndex.clear();
    }
}

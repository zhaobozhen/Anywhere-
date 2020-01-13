package com.absinthe.anywhere_.adapter;//打包 康姆点艾伯森斯点安妮薇儿下划线点鹅带坡特儿

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.HapticFeedbackConstants;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.model.QREntity;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.view.AnywhereEditor;
import com.absinthe.anywhere_.view.Editor;
import com.absinthe.anywhere_.view.QRCodeEditor;
import com.absinthe.anywhere_.view.SchemeEditor;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>//公共 类 基于鹅带坡特儿《威欸吃 扩展 回收者浏览了点景观持有人》扩展 回收者浏览了点鹅带坡特儿
        implements ItemTouchCallBack.OnItemTouchListener {
    public static final int ADAPTER_MODE_NORMAL = 0;//公共 静态 最终 整型 鹅带坡特儿下划线模式下划线普通的 等于 零
    public static final int ADAPTER_MODE_SORT = 1;
    public static final int ADAPTER_MODE_SELECT = 2;

    protected Context mContext;
    protected List<AnywhereEntity> mItems;
    protected Editor mEditor;
    protected int mode;
    private List<Integer> mSelectedIndex;

    protected BaseAdapter(Context context) {
        this.mContext = context;
        this.mItems = new ArrayList<>();
        this.mSelectedIndex = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    public void setItems(List<AnywhereEntity> items) {
        this.mItems.addAll(items);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void updateItems(List<AnywhereEntity> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffListCallback(items, this.mItems));
        this.mItems.clear();
        this.mItems.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public List<Integer> getSelectedIndex() {
        return mSelectedIndex;
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
        AnywhereEntity item = mItems.get(position);

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
                if (mSelectedIndex.contains(position)) {
                    holder.itemView.setScaleX(1.0f);
                    holder.itemView.setScaleY(1.0f);
                    ((MaterialCardView) holder.itemView).setChecked(false);
                    mSelectedIndex.remove((Integer) position);
                } else {
                    holder.itemView.setScaleX(0.9f);
                    holder.itemView.setScaleY(0.9f);
                    ((MaterialCardView) holder.itemView).setChecked(true);
                    mSelectedIndex.add(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {

                holder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                switch (type) {
                    case AnywhereType.URL_SCHEME:
                        openEditor(item, Editor.URL_SCHEME);
                        break;
                    case AnywhereType.ACTIVITY:
                        openEditor(item, Editor.ANYWHERE);
                        break;
                    case AnywhereType.MINI_PROGRAM:
                        break;
                    case AnywhereType.QR_CODE:
                        openEditor(item, Editor.QR_CODE);
                        break;
                }
                return true;
            }
            return false;
        });

        if (!mSelectedIndex.contains(position)) {
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
            ((MaterialCardView) holder.itemView).setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        //Todo Will be deleted in future version
        if (item.getAnywhereType() == AnywhereType.URL_SCHEME) {
            if (android.text.TextUtils.isEmpty(item.getParam2())) {
                item.setParam2(UiUtils.getPkgNameByUrl(mContext, item.getParam1()));
                AnywhereApplication.sRepository.update(item);
            }
        }

        if (item.getAnywhereType() != AnywhereType.QR_CODE) {
            String cmd = TextUtils.getItemCommand(item);
            if (!cmd.isEmpty()) {
                if (AppUtils.isAppFrozen(mContext, item)) {
                    if (ContextCompat.checkSelfPermission(AnywhereApplication.sContext, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                        if (PermissionUtils.isMIUI()) {
                            DialogManager.showGrantPriviligedPermDialog(mContext);
                        } else {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{IceBox.SDK_PERMISSION}, 0x233);
                        }
                    } else {
                        final OnAppUnfreezeListener onAppUnfreezeListener = () ->
                                MainActivity.getInstance().getViewModel().getCommand().setValue(cmd);
                        if (item.getAnywhereType() == AnywhereType.URL_SCHEME) {
                            PermissionUtils.unfreezeApp(mContext, item.getParam2(), onAppUnfreezeListener);
                        } else {
                            PermissionUtils.unfreezeApp(mContext, item.getParam1(), onAppUnfreezeListener);
                        }
                    }
                } else {
                    MainActivity.getInstance().getViewModel().getCommand().setValue(cmd);
                }
            }
        } else {
            QREntity entity = QRCollection.Singleton.INSTANCE.getInstance().getQREntity(item.getParam2());
            if (entity != null) {
                entity.launch();
            }
        }
    }

    private void openEditor(AnywhereEntity item, int type) {
        Editor.OnEditorListener listener = () -> deleteAnywhereActivity(item);

        switch (type) {
            case Editor.ANYWHERE:
                mEditor = new AnywhereEditor(mContext);
                break;
            case Editor.URL_SCHEME:
                mEditor = new SchemeEditor(mContext);
                break;
            case Editor.QR_CODE:
                mEditor = new QRCodeEditor(mContext);
                break;
        }

        mEditor.item(item)
                .isEditorMode(true)
                .isShortcut(item.getShortcutType() == AnywhereType.SHORTCUTS)
                .isExported(item.getExportedType() == AnywhereType.EXPORTED)
                .setOnEditorListener(listener)
                .build();

        mEditor.show();
    }

    private void deleteAnywhereActivity(AnywhereEntity ae) {
        DialogManager.showDeleteAnywhereDialog(mContext, ae);
    }

    public void updateSortedList() {
        new Thread(() -> {
            MainFragment.getViewModelInstance().refreshLock = true;
            long startTime = System.currentTimeMillis();

            for (int iter = 0, len = mItems.size(); iter < len; iter++) {
                AnywhereEntity item = mItems.get(iter);
                item.setTimeStamp(startTime - iter * 100 + "");
                AnywhereApplication.sRepository.update(item);
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
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {

    }

    public void deleteSelect() {
        if (mSelectedIndex.size() == 0) {
            return;
        }

        List<AnywhereEntity> list = new ArrayList<>();
        for (int index : mSelectedIndex) {
            if (index < mItems.size()) {
                list.add(mItems.get(index));
            }
        }
        for (AnywhereEntity ae : list) {
            mItems.remove(ae);
            AnywhereApplication.sRepository.delete(ae);
        }
        clearSelect();
    }

    public void clearSelect() {
        mSelectedIndex.clear();
    }
}

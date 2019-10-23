package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.CardItemViewBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.VibratorUtil;
import com.absinthe.anywhere_.view.Editor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectableCardsAdapter extends RecyclerView.Adapter<SelectableCardsAdapter.ItemViewHolder> implements ItemTouchCallBack.OnItemTouchListener {
    public static final int ADAPTER_MODE_NORMAL = 0;
    public static final int ADAPTER_MODE_SORT = 1;

    private List<AnywhereEntity> items;
    private Context mContext;
    private Editor mEditor;
    private int mode;

    public SelectableCardsAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
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

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardItemViewBinding binding = CardItemViewBinding.inflate(inflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);

        int type = item.getAnywhereType();
        LogUtil.d(this.getClass(), "Type = " + type);

        viewHolder.binding.itemCard.setOnClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {
                openAnywhereActivity(item);
            }
        });
        viewHolder.binding.itemCard.setOnLongClickListener(view -> {
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

        switch (type) {
            case AnywhereType.URL_SCHEME:
                viewHolder.binding.tvParam1.setVisibility(View.VISIBLE);
                viewHolder.binding.tvParam2.setVisibility(View.GONE);
                viewHolder.binding.tvParam3.setVisibility(View.GONE);
                break;
            case AnywhereType.ACTIVITY:
            case AnywhereType.MINI_PROGRAM:
                viewHolder.binding.tvParam1.setVisibility(View.VISIBLE);
                viewHolder.binding.tvParam2.setVisibility(View.VISIBLE);
                viewHolder.binding.tvParam3.setVisibility(View.GONE);
                break;
        }

        UiUtils.setVisibility(viewHolder.binding.tvDescription,
                !viewHolder.binding.tvDescription.getText().toString().isEmpty());
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        String cmd = TextUtils.getItemCommand(item);
        if (!cmd.isEmpty()) {
            MainFragment.getViewModelInstance().getCommand().setValue(cmd);
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
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(mContext.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
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
    public int getItemCount() {
        return items.size();
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardItemViewBinding binding;

        ItemViewHolder(CardItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(AnywhereEntity item) {
            binding.executePendingBindings();

            binding.setAppName(item.getAppName());
            binding.setParam1(item.getParam1());
            binding.setParam2(item.getParam2());
            binding.setParam3(item.getParam3());
            binding.setDescription(item.getDescription());
            binding.ivAppIcon.setImageDrawable(UiUtils.getAppIconByPackageName(mContext, item));
            if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                binding.ivBadge.setImageResource(R.drawable.ic_added_shortcut);
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else {
                binding.ivBadge.setVisibility(View.GONE);
            }
        }

    }

}


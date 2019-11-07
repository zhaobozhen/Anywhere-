package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.ItemCardViewBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.VibratorUtil;
import com.absinthe.anywhere_.view.Editor;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;

public class SelectableCardsAdapter extends BaseAdapter<SelectableCardsAdapter.ItemViewHolder> implements ItemTouchCallBack.OnItemTouchListener {

    public SelectableCardsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.items = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCardViewBinding binding = ItemCardViewBinding.inflate(inflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);

        int type = item.getAnywhereType();
        LogUtil.d(this.getClass(), "Type = " + type);

        viewHolder.binding.itemCard.setOnClickListener(view -> {
            if (mode == ADAPTER_MODE_NORMAL) {
                openAnywhereActivity(item);
                notifyItemChanged(position);
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

        UiUtils.setVisibility(viewHolder.binding.tvDescription, !item.getDescription().isEmpty());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemCardViewBinding binding;

        ItemViewHolder(ItemCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(AnywhereEntity item) {
            binding.executePendingBindings();

            try {
                if (IceBox.getAppEnabledSetting(mContext, item.getParam1()) != 0) {
                    binding.setAppName(item.getAppName() + "\u2744");
                } else {
                    binding.setAppName(item.getAppName());
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                binding.setAppName(item.getAppName());
            }

            binding.setParam1(item.getParam1());
            binding.setParam2(item.getParam2());
            binding.setParam3(item.getParam3());
            binding.setDescription(item.getDescription());
            binding.ivAppIcon.setImageDrawable(UiUtils.getAppIconByPackageName(mContext, item));
            if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                binding.ivBadge.setImageResource(R.drawable.ic_added_shortcut);
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else if (item.getExportedType() == AnywhereType.EXPORTED) {
                binding.ivBadge.setImageResource(R.drawable.ic_exported);
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else {
                binding.ivBadge.setVisibility(View.GONE);
            }
        }

    }

}


package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.databinding.ItemStreamCardViewBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;

public class StreamCardsAdapter extends BaseAdapter<StreamCardsAdapter.ItemViewHolder> implements ItemTouchCallBack.OnItemTouchListener {

    public StreamCardsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.items = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemStreamCardViewBinding binding = ItemStreamCardViewBinding.inflate(inflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);

//        UiUtils.setVisibility(viewHolder.binding.tvDescription, !item.getDescription().isEmpty());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemStreamCardViewBinding binding;

        ItemViewHolder(ItemStreamCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AnywhereEntity item) {
            binding.executePendingBindings();

            try {
                if (IceBox.getAppEnabledSetting(mContext, item.getParam1()) != 0) {
                    binding.setAppName(item.getAppName() + "\u2744");
                } else {
                    binding.setAppName(item.getAppName());
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Logger.e(e.getMessage());
                binding.setAppName(item.getAppName());
            }

            binding.setDescription(item.getDescription());
            Glide.with(mContext)
                    .load(UiUtils.getAppIconByPackageName(mContext, item))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.ivAppIcon);
            if (GlobalValues.sIsCardBackground) {
                UiUtils.setCardUseIconColor(binding.ivCardBg, UiUtils.getAppIconByPackageName(mContext, item));
            }

            if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                binding.ivBadge.setImageResource(R.drawable.ic_add_shortcut);
                binding.ivBadge.setColorFilter(mContext.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else if (item.getExportedType() == AnywhereType.EXPORTED) {
                binding.ivBadge.setImageResource(R.drawable.ic_exported);
                binding.ivBadge.setColorFilter(mContext.getResources().getColor(R.color.exported_tint), PorterDuff.Mode.SRC_IN);
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else {
                binding.ivBadge.setVisibility(View.GONE);
            }

        }

    }

}
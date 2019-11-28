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
import com.absinthe.anywhere_.databinding.ItemStreamCardSingleLineBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;

public class SingleLineStreamCardsAdapter extends BaseAdapter<SingleLineStreamCardsAdapter.ItemViewHolder> implements ItemTouchCallBack.OnItemTouchListener {

    public SingleLineStreamCardsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.items = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemStreamCardSingleLineBinding binding = ItemStreamCardSingleLineBinding.inflate(inflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemStreamCardSingleLineBinding binding;

        ItemViewHolder(ItemStreamCardSingleLineBinding binding) {
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
                LogUtil.e(e.getMessage());
                binding.setAppName(item.getAppName());
            }

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
                binding.ivBadge.setVisibility(View.VISIBLE);
            } else {
                binding.ivBadge.setVisibility(View.GONE);
            }

        }

    }

}
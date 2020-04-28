package com.absinthe.anywhere_.adapter.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.BaseAdapter;
import com.absinthe.anywhere_.adapter.ItemTouchCallBack;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.databinding.ItemStreamCardSingleLineBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;

public class SingleLineStreamCardsAdapter extends BaseAdapter<SingleLineStreamCardsAdapter.ItemViewHolder> implements ItemTouchCallBack.OnItemTouchListener {

    public SingleLineStreamCardsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.mItems = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                ItemStreamCardSingleLineBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        AnywhereEntity item = mItems.get(position);
        viewHolder.bind(item);

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemStreamCardSingleLineBinding binding;

        ItemViewHolder(ItemStreamCardSingleLineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void bind(AnywhereEntity item) {

            String pkgName = item.getPackageName();

            try {
                if (IceBox.getAppEnabledSetting(mContext, pkgName) != 0) {
                    binding.tvAppName.setText("\u2744" + item.getAppName());
                } else {
                    binding.tvAppName.setText(item.getAppName());
                }
            } catch (PackageManager.NameNotFoundException e) {
                binding.tvAppName.setText(item.getAppName());
            }

            if (item.getIconUri().isEmpty()) {
                Glide.with(mContext)
                        .load(UiUtils.getAppIconByPackageName(mContext, item))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.ivAppIcon);
            } else {
                Glide.with(mContext)
                        .load(item.getIconUri())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.ivAppIcon);
            }

            if (GlobalValues.INSTANCE.getSCardBackgroundMode().equals(Const.CARD_BG_MODE_PURE)) {
//                binding.blurLayout.setVisibility(View.GONE);

                if (item.getColor() == 0) {
                    UiUtils.setCardUseIconColor(binding.ivCardBg,
                            UiUtils.getAppIconByPackageName(mContext, item),
                            color -> {
                                if (color != 0) {
                                    binding.tvAppName.setTextColor(UiUtils.isLightColor(color) ? Color.BLACK : Color.WHITE);
                                } else {
                                    binding.tvAppName.setTextColor(mContext.getResources().getColor(R.color.textColorNormal));
                                }
                            });
                } else {
                    binding.ivCardBg.setBackgroundColor(item.getColor());
                    binding.tvAppName.setTextColor(UiUtils.isLightColor(item.getColor()) ? Color.BLACK : Color.WHITE);
                }
            } else if (GlobalValues.INSTANCE.getSCardBackgroundMode().equals(Const.CARD_BG_MODE_GRADIENT)) {
//                binding.blurLayout.setVisibility(View.GONE);

                if (item.getColor() == 0) {
                    UiUtils.setCardUseIconColor(binding.ivCardBg, UiUtils.getAppIconByPackageName(mContext, item));
                } else {
                    UiUtils.createLinearGradientBitmap(binding.ivCardBg, item.getColor(), Color.TRANSPARENT);
                }
            } else if (GlobalValues.INSTANCE.getSCardBackgroundMode().equals(Const.CARD_BG_MODE_BLURRY)) {
//                binding.blurLayout.startBlur();
                binding.ivCardBg.setVisibility(View.GONE);
                binding.itemCard.setCardBackgroundColor(Color.TRANSPARENT);
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
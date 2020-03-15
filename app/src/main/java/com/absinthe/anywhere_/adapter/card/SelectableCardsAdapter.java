package com.absinthe.anywhere_.adapter.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.BaseAdapter;
import com.absinthe.anywhere_.adapter.ItemTouchCallBack;
import com.absinthe.anywhere_.databinding.ItemCardViewBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;

import timber.log.Timber;

public class SelectableCardsAdapter extends BaseAdapter<SelectableCardsAdapter.ItemViewHolder>
        implements ItemTouchCallBack.OnItemTouchListener {

    public SelectableCardsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.mItems = new ArrayList<>();
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
        AnywhereEntity item = mItems.get(position);
        viewHolder.bind(item);

        UiUtils.setVisibility(viewHolder.binding.tvDescription, !item.getDescription().isEmpty());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemCardViewBinding binding;

        ItemViewHolder(ItemCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        private void bind(AnywhereEntity item) {

            int type = item.getAnywhereType();
            String pkgName;

            if (type == AnywhereType.URL_SCHEME) {
                pkgName = item.getParam2();
            } else {
                pkgName = item.getParam1();
            }
            try {
                if (IceBox.getAppEnabledSetting(mContext, pkgName) != 0) {
                    binding.tvAppName.setText(item.getAppName() + "\u2744");
                } else {
                    binding.tvAppName.setText(item.getAppName());
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Timber.e(e);
                binding.tvAppName.setText(item.getAppName());
            }

            binding.tvParam1.setText(item.getParam1());
            binding.tvParam2.setText(item.getParam2());
            binding.tvDescription.setText(item.getDescription());

            switch (type) {
                case AnywhereType.URL_SCHEME:
                    binding.tvParam2.setVisibility(View.GONE);
                    break;
                case AnywhereType.QR_CODE:
                    binding.tvParam1.setVisibility(View.GONE);
                    binding.tvParam2.setVisibility(View.GONE);
                    break;
                default:
            }

            Glide.with(mContext)
                    .load(UiUtils.getAppIconByPackageName(mContext, item))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.ivAppIcon);

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
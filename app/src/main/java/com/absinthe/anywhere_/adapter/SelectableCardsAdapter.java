package com.absinthe.anywhere_.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;

import com.absinthe.anywhere_.R;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class SelectableCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AnywhereItem> items;

    private SelectionTracker<Long> selectionTracker;

    public SelectableCardsAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<AnywhereItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AnywhereItem item = items.get(position);
        ((ItemViewHolder) viewHolder).bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final Details details;
        private final MaterialCardView materialCardView;
        private final TextView appNameView;
        private final TextView packageNameView;
        private final TextView classNameView;
        private final TextView customTextureView;

        ItemViewHolder(View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_card);
            appNameView = itemView.findViewById(R.id.tv_card_app_name);
            packageNameView = itemView.findViewById(R.id.tv_card_package_name);
            classNameView = itemView.findViewById(R.id.tv_card_class_name);
            customTextureView = itemView.findViewById(R.id.tv_card_custom_texture);
            details = new Details();
        }

        private void bind(AnywhereItem item, int position) {
            details.position = position;
            appNameView.setText(item.appName);
            packageNameView.setText(item.packageName);
            classNameView.setText(item.className);
            customTextureView.setText(item.customTexture);
            if (selectionTracker != null) {
                bindSelectedState();
            }
        }

        private void bindSelectedState() {
            materialCardView.setChecked(selectionTracker.isSelected(details.getSelectionKey()));
        }

        ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return details;
        }
    }

    public static class DetailsLookup extends ItemDetailsLookup<Long> {

        private final RecyclerView recyclerView;

        public DetailsLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof ItemViewHolder) {
                    return ((ItemViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }

    public static class KeyProvider extends ItemKeyProvider<Long> {

        public KeyProvider(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return (long) position;
        }

        @Override
        public int getPosition(@NonNull Long key) {
            long value = key;
            return (int) value;
        }
    }

    public static class AnywhereItem {

        private final String packageName;
        private final String className;
        private final String appName;
        private final String customTexture;

        public AnywhereItem(String packageName, String className, String appName, String customTexture) {
            this.packageName = packageName;
            this.className = className;
            this.appName = appName;
            this.customTexture = "Custom Texture";
        }
    }

    public static class Details extends ItemDetailsLookup.ItemDetails<Long> {

        long position;

        Details() {
        }

        @Override
        public int getPosition() {
            return (int) position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public boolean inDragRegion(@NonNull MotionEvent e) {
            return true;
        }
    }
}


package com.absinthe.anywhere_.adapter.page;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

    private List<AnywhereEntity> mList;
    private String mCategory;

    ChipAdapter(String category) {
        mCategory = category;
        mList = new ArrayList<>();

        List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (list != null) {
            for (AnywhereEntity item : list) {
                if ((TextUtils.isEmpty(item.getCategory()) && mCategory.equals(AnywhereType.DEFAULT_CATEGORY))
                || item.getCategory().equals(mCategory)) {
                    mList.add(item);
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position));

        holder.chip.setOnClickListener(v ->
                CommandUtils.execCmd(com.absinthe.anywhere_.utils.TextUtils.getItemCommand(mList.get(position))));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Chip chip;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }

        private void bind(AnywhereEntity item) {
            chip.setText(item.getAppName());
            chip.setChipIcon(UiUtils.getAppIconByPackageName(AnywhereApplication.sContext, item));
        }
    }
}

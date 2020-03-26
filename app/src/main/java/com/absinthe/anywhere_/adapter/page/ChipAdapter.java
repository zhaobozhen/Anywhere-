package com.absinthe.anywhere_.adapter.page;

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
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.ActivityStackManager;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

    private List<AnywhereEntity> mList;

    ChipAdapter(String category) {
        mList = new ArrayList<>();

        List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (list != null) {
            for (AnywhereEntity item : list) {
                if ((TextUtils.isEmpty(item.getCategory()) && category.equals(AnywhereType.DEFAULT_CATEGORY))
                        || item.getCategory().equals(category)) {
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

        holder.chip.setOnClickListener(v -> {
            AnywhereEntity ae = mList.get(position);
            if (ae.getAnywhereType() == AnywhereType.IMAGE) {
                DialogManager.INSTANCE.showImageDialog(ActivityStackManager.getInstance().getTopActivity(), ae);
            } else if (ae.getAnywhereType() == AnywhereType.SHELL) {
                String result = CommandUtils.execAdbCmd(ae.getParam1());
                DialogManager.showShellResultDialog(ActivityStackManager.getInstance().getTopActivity(), result, null, null);
            } else {
                CommandUtils.execCmd(TextUtils.getItemCommand(ae));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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

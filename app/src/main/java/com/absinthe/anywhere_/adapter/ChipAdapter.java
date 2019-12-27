package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.UiUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

    private Context mContext;
    private List<AnywhereEntity> mList;

    public ChipAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        for (int i = 0 ; i < 5; i++) {
            mList.add(new AnywhereEntity("","","","","","",1,""));
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

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Chip chip;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }

        private void bind(AnywhereEntity item) {
            chip.setText(item.getAppName());
            chip.setChipIcon(UiUtils.getAppIconByPackageName(mContext, item.getParam1()));
        }
    }
}

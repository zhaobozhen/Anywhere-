package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.R;

import java.util.ArrayList;
import java.util.List;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mList;

    public PageListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mList.add("Default");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private RecyclerView rvChip;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            rvChip = itemView.findViewById(R.id.rv_chip);
        }

        private void bind(String title) {
            tvTitle.setText(title);
            ChipAdapter adapter = new ChipAdapter(mContext);
            rvChip.setAdapter(adapter);
            rvChip.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL));
        }
    }
}

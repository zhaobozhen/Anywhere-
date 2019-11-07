package com.absinthe.anywhere_.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.list.AppDetailActivity;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.Editor;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    public static final int MODE_APP_LIST = 0;
    public static final int MODE_APP_DETAIL = 1;

    private Context mContext;
    private List<AppListBean> mList;
    private int mode;

    public AppListAdapter(Context context, int mode) {
        mContext = context;
        mList = new ArrayList<>();
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position));
        AppListBean item = mList.get(position);

        holder.clAppList.setOnClickListener(view -> {
            if (mode == MODE_APP_LIST) {
                Intent intent = new Intent(mContext, AppDetailActivity.class);
                intent.putExtra(Const.INTENT_EXTRA_APP_NAME, item.getAppName());
                intent.putExtra(Const.INTENT_EXTRA_PKG_NAME, item.getPackageName());
                mContext.startActivity(intent);
            } else {
                String timeStamp = System.currentTimeMillis() + "";
                AnywhereEntity ae = new AnywhereEntity(timeStamp, item.getAppName(),
                        item.getPackageName(),
                        item.getClassName().replace(item.getPackageName(), ""),
                        "", "", AnywhereType.ACTIVITY, timeStamp);
                Editor editor = new Editor(mContext, Editor.ANYWHERE)
                        .item(ae)
                        .isEditorMode(false)
                        .isShortcut(false)
                        .build();
                editor.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<AppListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvAppName;
        private TextView tvPkgName;
        private ConstraintLayout clAppList;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_app_icon);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvPkgName = itemView.findViewById(R.id.tv_pkg_name);
            clAppList = itemView.findViewById(R.id.cl_app_list);
        }

        private void bind(AppListBean item) {
            if (mode == MODE_APP_LIST) {
                ivIcon.setImageDrawable(item.getIcon());
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getPackageName());
            } else {
                ivIcon.setImageDrawable(UiUtils.getActivityIcon(mContext, new ComponentName(item.getPackageName(), item.getClassName())));
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getClassName());
            }
        }
    }
}

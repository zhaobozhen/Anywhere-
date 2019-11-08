package com.absinthe.anywhere_.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.list.AppDetailActivity;
import com.absinthe.anywhere_.ui.settings.IconPackDialogFragment;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.Editor;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> implements Filterable {
    public static final int MODE_APP_LIST = 0;
    public static final int MODE_APP_DETAIL = 1;
    public static final int MODE_ICON_PACK = 2;

    private Context mContext;
    private List<AppListBean> mList, tempList;
    private ListFilter filter;
    private int mode;
    private IconPackDialogFragment iconPackDialogFragment;

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
            } else if (mode == MODE_APP_DETAIL) {
                String timeStamp = System.currentTimeMillis() + "";
                int exported = 0;
                if (UiUtils.isActivityExported(mContext, new ComponentName(item.getPackageName(),
                        item.getClassName()))) {
                    exported = 100;
                }

                AnywhereEntity ae = new AnywhereEntity(timeStamp, item.getAppName(),
                        item.getPackageName(),
                        item.getClassName().replace(item.getPackageName(), ""),
                        "", "", AnywhereType.ACTIVITY + exported, timeStamp);
                Editor editor = new Editor(mContext, Editor.ANYWHERE)
                        .item(ae)
                        .isEditorMode(false)
                        .isShortcut(false)
                        .build();
                editor.show();
            } else if (mode == MODE_ICON_PACK) {
                GlobalValues.setsIconPack(item.getPackageName());
                ToastUtil.makeText("重启生效");
                if (iconPackDialogFragment != null) {
                    iconPackDialogFragment.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<AppListBean> list) {
        mList = list;
        tempList = list;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ListFilter();
        }
        return filter;
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
            } else if (mode == MODE_APP_DETAIL) {
                ivIcon.setImageDrawable(UiUtils.getActivityIcon(mContext, new ComponentName(item.getPackageName(), item.getClassName())));
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getClassName());
            } else if (mode == MODE_ICON_PACK) {
                ivIcon.setImageDrawable(UiUtils.getAppIconByPackageName(mContext, item.getPackageName()));
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getPackageName());
            }
        }
    }

    class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AppListBean> newList = new ArrayList<>();

            if (constraint != null && constraint.toString().trim().length() > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    String content = tempList.get(i).getAppName() + tempList.get(i).getClassName();
                    if (TextUtils.containsIgnoreCase(content, constraint.toString())) {
                        newList.add(tempList.get(i));
                    }
                }
            } else {
                newList = tempList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = newList.size();
            filterResults.values = newList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //这里对number进行过滤后重新赋值
            mList = (List<AppListBean>) results.values;
            //如果过滤后的返回的值的个数大于等于0的话,对Adapter的界面进行刷新
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                //否则说明没有任何过滤的结果,直接提示用户"没有符合条件的结果"
                mList = new ArrayList<>();
                notifyDataSetChanged();
            }

        }
    }

    public void setIconPackDialogFragment(IconPackDialogFragment fragment) {
        iconPackDialogFragment = fragment;
    }
}

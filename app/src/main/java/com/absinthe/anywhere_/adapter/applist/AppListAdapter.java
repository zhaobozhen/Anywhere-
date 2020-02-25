package com.absinthe.anywhere_.adapter.applist;

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
import com.absinthe.anywhere_.view.editor.AnywhereEditor;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> implements Filterable {
    public static final int MODE_APP_LIST = 0;
    public static final int MODE_APP_DETAIL = 1;
    public static final int MODE_ICON_PACK = 2;
    public static final int MODE_CARD_LIST = 3;

    private Context mContext;
    private List<AppListBean> mList, mTempList;
    private ListFilter mFilter;
    private int mMode;
    private IconPackDialogFragment mIconPackDialogFragment;
    private OnItemClickListener mListener;

    public AppListAdapter(Context context, int mode) {
        mContext = context;
        mList = new ArrayList<>();
        this.mMode = mode;
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
            if (mMode == MODE_APP_LIST) {
                Intent intent = new Intent(mContext, AppDetailActivity.class);
                intent.putExtra(Const.INTENT_EXTRA_APP_NAME, item.getAppName());
                intent.putExtra(Const.INTENT_EXTRA_PKG_NAME, item.getPackageName());
                mContext.startActivity(intent);
            } else if (mMode == MODE_APP_DETAIL) {
                int exported = 0;
                if (UiUtils.isActivityExported(mContext, new ComponentName(item.getPackageName(),
                        item.getClassName()))) {
                    exported = 100;
                }

                AnywhereEntity ae = AnywhereEntity.Builder();
                ae.setAppName(item.getAppName());
                ae.setParam1(item.getPackageName());
                ae.setParam2(item.getClassName().trim().replace(item.getPackageName(), ""));
                ae.setType(AnywhereType.ACTIVITY + exported);

                AnywhereEditor editor = new AnywhereEditor(mContext)
                        .item(ae)
                        .isEditorMode(false)
                        .isShortcut(false)
                        .build();
                editor.show();
            } else if (mMode == MODE_ICON_PACK) {
                GlobalValues.setsIconPack(item.getPackageName());
                mIconPackDialogFragment.requireActivity().finish();
                ToastUtil.makeText(R.string.toast_restart_to_active);
                if (mIconPackDialogFragment != null) {
                    mIconPackDialogFragment.dismiss();
                }
            } else if (mMode == MODE_CARD_LIST) {
                if (mListener != null) {
                    mListener.onClick(mList.get(position), position);
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
        mTempList = list;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ListFilter();
        }
        return mFilter;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
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
            if (mMode == MODE_APP_LIST) {
                ivIcon.setImageDrawable(item.getIcon());
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getPackageName());
            } else if (mMode == MODE_APP_DETAIL) {
                ivIcon.setImageDrawable(UiUtils.getActivityIcon(mContext, new ComponentName(item.getPackageName(), item.getClassName())));
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getClassName());
            } else if (mMode == MODE_CARD_LIST) {
                ivIcon.setImageDrawable(UiUtils.getAppIconByPackageName(mContext, item));
                tvAppName.setText(item.getAppName());
                tvPkgName.setText(item.getClassName());
            } else if (mMode == MODE_ICON_PACK) {
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
                for (int i = 0, len = mTempList.size(); i < len; i++) {
                    //Match App's name and package name
                    AppListBean bean = mTempList.get(i);
                    String content = bean.getAppName() + bean.getPackageName() + bean.getClassName();
                    if (TextUtils.containsIgnoreCase(content, constraint.toString())) {
                        newList.add(mTempList.get(i));
                    }
                }
            } else {
                newList = mTempList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = newList.size();
            filterResults.values = newList;
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //这里对number进行过滤后重新赋值
            mList = (List<AppListBean>) results.values;
            //如果过滤后的返回的值的个数大于等于 0 的话,对 Adapter 的界面进行刷新
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
        mIconPackDialogFragment = fragment;
    }

    public interface OnItemClickListener {
        void onClick(AppListBean bean, int which);
    }
}

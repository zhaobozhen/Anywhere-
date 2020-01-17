package com.absinthe.anywhere_.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.CardListDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class PageListDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private CardListDialogBuilder mBuilder;
    private List<PageEntity> mList;
    private AppListAdapter.OnItemClickListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new CardListDialogBuilder(mContext);
        if (mListener != null) {
            mBuilder.setOnItemClickListener(mListener);
        }
        initView();

        return builder.setView(mBuilder.getRoot())
                .create();
    }

    public void setOnItemClickListener(AppListAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    private void initView() {
        List<AppListBean> listBeans = new ArrayList<>();

        mList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        if (mList != null) {
            for (PageEntity pe : mList) {
                listBeans.add(new AppListBean(pe.getTitle(), "", "", -1));
            }
            mBuilder.mAdapter.setList(listBeans);
        }
    }
}


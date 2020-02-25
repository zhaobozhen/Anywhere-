package com.absinthe.anywhere_.ui.list;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.CardListDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class CardListDialogFragment extends AnywhereDialogFragment {
    private CardListDialogBuilder mBuilder;
    private AppListAdapter.OnItemClickListener mListener;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(getContext());
        mBuilder = new CardListDialogBuilder(getContext());
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

    private void setDisplayPlaceholder(boolean flag) {
        if (flag) {
            ((ViewFlipper) mBuilder.getRoot()).setDisplayedChild(1);
        } else {
            ((ViewFlipper) mBuilder.getRoot()).setDisplayedChild(0);
        }
    }

    private void initView() {
        List<AppListBean> listBeans = new ArrayList<>();

        List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (list != null) {
            if (list.size() == 0) {
                setDisplayPlaceholder(true);
            } else {
                setDisplayPlaceholder(false);
                for (AnywhereEntity ae : list) {
                    if (ae.getAnywhereType() == AnywhereType.URL_SCHEME
                            || ae.getAnywhereType() == AnywhereType.IMAGE
                            || ae.getAnywhereType() == AnywhereType.SHELL) {
                        listBeans.add(new AppListBean(ae.getAppName(), ae.getParam2(), ae.getParam1(),
                                ae.getAnywhereType(), UiUtils.getAppIconByPackageName(getContext(), ae.getParam2())));
                    } else {
                        listBeans.add(new AppListBean(ae.getAppName(), ae.getParam1(), ae.getParam2(),
                                ae.getAnywhereType(), UiUtils.getAppIconByPackageName(getContext(), ae.getParam1())));
                    }
                }
                mBuilder.mAdapter.setList(listBeans);
            }
        }
    }
}

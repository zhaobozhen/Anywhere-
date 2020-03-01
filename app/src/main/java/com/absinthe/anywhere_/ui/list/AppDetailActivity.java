package com.absinthe.anywhere_.ui.list;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityAppDetailBinding;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.ListUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class AppDetailActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private ActivityAppDetailBinding mBinding;
    private AppListAdapter mAdapter;

    @Override
    protected void setViewBinding() {
        mBinding = ActivityAppDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = mBinding.toolbar;
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            mToolbar.setTitle(intent.getStringExtra(Const.INTENT_EXTRA_APP_NAME));
        }
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            initRecyclerView();
            initData(intent.getStringExtra(Const.INTENT_EXTRA_PKG_NAME));
        }
    }

    private void initRecyclerView() {
        mBinding.rvAppList.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mAdapter = new AppListAdapter(this, AppListAdapter.MODE_APP_DETAIL);
        mBinding.rvAppList.setAdapter(mAdapter);
    }

    private void initData(String pkgName) {
        mBinding.srlAppDetail.setEnabled(true);
        mBinding.srlAppDetail.setRefreshing(true);
        new Thread(() -> {
            List<AppListBean> list = new ArrayList<>();
            List<String> clazz = AppUtils.getActivitiesClass(this, pkgName);

            for (String s : clazz) {
                String appName = UiUtils.getActivityLabel(this, new ComponentName(pkgName, s));
                if (UiUtils.isActivityExported(this, new ComponentName(pkgName, s))) {
                    appName = appName + " (Exported)";
                }
                list.add(new AppListBean(appName, pkgName, s, -1));
            }

            runOnUiThread(() -> {
                if (list.isEmpty()) {
                    mBinding.vfContainer.setDisplayedChild(1);
                } else {
                    mAdapter.setList(ListUtils.sortAppListByExported(list));
                    mBinding.vfContainer.setDisplayedChild(0);
                }
                mBinding.srlAppDetail.setRefreshing(false);
                mBinding.srlAppDetail.setEnabled(false);
            });

        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_detail_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(true);    // Display "Start search" button
            searchView.setQueryRefinementEnabled(true);
            searchView.setOnQueryTextListener(this);

            LinearLayout searchBar = searchView.findViewById(R.id.search_bar);
            searchBar.setLayoutTransition(new LayoutTransition());
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return false;
    }
}

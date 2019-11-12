package com.absinthe.anywhere_.ui.list;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.AppListAdapter;
import com.absinthe.anywhere_.adapter.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.ListUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppDetailActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private AppListAdapter adapter;
    private SwipeRefreshLayout srlAppDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(intent.getStringExtra(Const.INTENT_EXTRA_APP_NAME));
        }

        initView();
        initRecyclerView();
        initData(Objects.requireNonNull(intent).getStringExtra(Const.INTENT_EXTRA_PKG_NAME));
    }

    private void initView() {
        srlAppDetail = findViewById(R.id.srl_app_detail);

        //Bug of DayNight lib
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.resetColorPrimary));
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_app_list);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        adapter = new AppListAdapter(this, AppListAdapter.MODE_APP_DETAIL);
        recyclerView.setAdapter(adapter);
    }

    private void initData(String pkgName) {
        srlAppDetail.setEnabled(true);
        srlAppDetail.setRefreshing(true);
        new Thread(() -> {
            List<AppListBean> list = new ArrayList<>();
            List<String> clazz = AppUtils.getActivitiesClass(this, pkgName);

            for (String s : clazz) {
                String appName = UiUtils.getActivityLabel(this, new ComponentName(pkgName, s));
                if (UiUtils.isActivityExported(this, new ComponentName(pkgName, s))) {
                    appName = appName + " (Exported)";
                }
                list.add(new AppListBean(appName, pkgName, s));
            }

            runOnUiThread(() -> {
                adapter.setList(ListUtils.sortAppListByExported(list));
                srlAppDetail.setRefreshing(false);
                srlAppDetail.setEnabled(false);
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
            searchView.setSubmitButtonEnabled(true);    // 显示“开始搜索”的按钮
            searchView.setQueryRefinementEnabled(true); // 提示内容右边提供一个将提示内容放到搜索框的
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
        adapter.getFilter().filter(newText);
        return false;
    }
}

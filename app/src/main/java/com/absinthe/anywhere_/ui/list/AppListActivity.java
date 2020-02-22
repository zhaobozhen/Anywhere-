package com.absinthe.anywhere_.ui.list;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityAppListBinding;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import java.util.List;

public class AppListActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private ActivityAppListBinding binding;
    private AppListAdapter mAdapter;
    private boolean isShowSystemApp;

    @Override
    protected void setViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_list);
    }

    @Override
    protected void setToolbar() {
        mToolbar = binding.toolbar.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowSystemApp = false;

        initRecyclerView();
        initData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_list_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem showSystemApp = menu.findItem(R.id.show_system_app);

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(true);    // show search button
            searchView.setQueryRefinementEnabled(true);
            searchView.setOnQueryTextListener(this);

            LinearLayout searchBar = searchView.findViewById(R.id.search_bar);
            searchBar.setLayoutTransition(new LayoutTransition());
        }
        // Bug of DayNight lib
        if (showSystemApp != null) {
            if (UiUtils.isDarkMode(this)) {
                showSystemApp.setTitle(Html.fromHtml("<font color='#FFFFFF'>" + showSystemApp.getTitle() + "</font>"));
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.show_system_app) {
            if (item.getTitle().toString().equals(getString(R.string.menu_show_system_app))) {
                item.setTitle(R.string.menu_hide_system_app);
                if (UiUtils.isDarkMode(this)) {
                    item.setTitle(Html.fromHtml("<font color='#FFFFFF'>" + item.getTitle() + "</font>"));
                }
                isShowSystemApp = true;
            } else {
                item.setTitle(R.string.menu_show_system_app);
                if (UiUtils.isDarkMode(this)) {
                    item.setTitle(Html.fromHtml("<font color='#FFFFFF'>" + item.getTitle() + "</font>"));
                }
                isShowSystemApp = false;
            }
            initData(isShowSystemApp);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initView() {
        super.initView();

        binding.srlAppList.setOnRefreshListener(() -> initData(isShowSystemApp));

        //Bug of DayNight lib
        if (UiUtils.isDarkMode(this)) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.resetColorPrimary));
        }
    }

    private void initRecyclerView() {
        binding.rvAppList.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mAdapter = new AppListAdapter(this, AppListAdapter.MODE_APP_LIST);
        binding.rvAppList.setAdapter(mAdapter);

        binding.rvAppList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        recyclerView.getChildCount() == 0 ? 0 : recyclerView.getChildAt(0).getTop();
                binding.srlAppList.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void initData(boolean showSystem) {
        binding.srlAppList.setRefreshing(true);
        new Thread(() -> {
            List<AppListBean> list = AppUtils.getAppList(getPackageManager(), showSystem);

            runOnUiThread(() -> {
                mAdapter.setList(list);
                binding.srlAppList.setRefreshing(false);
            });
        }).start();
    }

    private void initData() {
        initData(false);
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

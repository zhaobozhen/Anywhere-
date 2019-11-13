package com.absinthe.anywhere_.ui.list;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.AppListAdapter;
import com.absinthe.anywhere_.adapter.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityAppListBinding;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.utils.AppUtils;

import java.util.List;

public class AppListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ActivityAppListBinding binding;
    private AppListAdapter adapter;
    private boolean isShowSystemApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_list);
        isShowSystemApp = false;

        initView();
        initRecyclerView();
        initData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_list_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
//        MenuItem showSystemApp = menu.findItem(R.id.show_system_app);

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(true);    // 显示“开始搜索”的按钮
            searchView.setQueryRefinementEnabled(true); // 提示内容右边提供一个将提示内容放到搜索框的
            searchView.setOnQueryTextListener(this);

            LinearLayout searchBar = searchView.findViewById(R.id.search_bar);
            searchBar.setLayoutTransition(new LayoutTransition());
        }
//        // Bug of DayNight lib
//        if (showSystemApp != null) {
//            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//                showSystemApp.setTitle("Night");
//            }
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.show_system_app) {
            if (item.getTitle().toString().equals(getString(R.string.menu_show_system_app))) {
                item.setTitle(R.string.menu_hide_system_app);
                isShowSystemApp = true;
            } else {
                item.setTitle(R.string.menu_show_system_app);
                isShowSystemApp = false;
            }
            initData(isShowSystemApp);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        binding.srlAppList.setOnRefreshListener(() -> initData(isShowSystemApp));

        //Bug of DayNight lib
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.resetColorPrimary));
        }
    }

    private void initRecyclerView() {
        binding.rvAppList.setLayoutManager(new WrapContentLinearLayoutManager(this));
        adapter = new AppListAdapter(this, AppListAdapter.MODE_APP_LIST);
        binding.rvAppList.setAdapter(adapter);

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
                adapter.setList(list);
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
        adapter.getFilter().filter(newText);
        return false;
    }
}

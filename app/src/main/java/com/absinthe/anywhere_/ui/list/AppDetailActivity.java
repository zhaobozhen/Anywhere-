package com.absinthe.anywhere_.ui.list;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.AppListAdapter;
import com.absinthe.anywhere_.adapter.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppDetailActivity extends AppCompatActivity {
    private AppListAdapter adapter;

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

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_app_list);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        adapter = new AppListAdapter(this, AppListAdapter.MODE_APP_DETAIL);
        recyclerView.setAdapter(adapter);
    }

    private void initData(String pkgName) {
        new Thread(() -> {
            List<AppListBean> list = new ArrayList<>();
            List<String> clazz = AppUtils.getActivitiesClass(this, pkgName);

            for (String s : clazz) {
                list.add(new AppListBean(UiUtils.getActivityLabel(this, new ComponentName(pkgName, s)), pkgName, s));
            }

            runOnUiThread(() -> adapter.setList(list));

        }).start();
    }
}

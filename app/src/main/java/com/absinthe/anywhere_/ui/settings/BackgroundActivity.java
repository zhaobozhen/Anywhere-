package com.absinthe.anywhere_.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.background.BackgroundAdapter;
import com.absinthe.anywhere_.adapter.background.BackgroundNode;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;

import java.util.ArrayList;
import java.util.List;

public class BackgroundActivity extends BaseActivity {

    private static BackgroundActivity sInstance;
    private BackgroundAdapter mAdapter;

    public static BackgroundActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;

        if (!IzukoHelper.isHitagi()) {
            finish();
        }

        setContentView(R.layout.activity_background);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BackgroundAdapter();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
            setDocumentResultListener(uri -> {
                BackgroundNode node = mAdapter.getItem(position);
                if (node != null) {
                    node.setBackground(uri.toString());
                    mAdapter.setData(position, node);
                }
            });
        });
        rvList.setAdapter(mAdapter);

        List<PageEntity> pageEntityList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        List<BackgroundNode> list = new ArrayList<>();
        if (pageEntityList != null) {
            for (PageEntity pe : pageEntityList) {
                BackgroundNode node = new BackgroundNode();
                node.setTitle(pe.getTitle());
                node.setBackground(GlobalValues.sBackgroundUri);
                list.add(node);
            }
        }
        mAdapter.addData(list);
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }
}

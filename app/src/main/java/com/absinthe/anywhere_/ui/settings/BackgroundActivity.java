package com.absinthe.anywhere_.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.adapter.background.BackgroundAdapter;
import com.absinthe.anywhere_.databinding.ActivityBackgroundBinding;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;

import java.util.List;

public class BackgroundActivity extends BaseActivity {

    private static BackgroundActivity sInstance;
    private BackgroundAdapter mAdapter;
    private ActivityBackgroundBinding mBinding;

    public static BackgroundActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void setViewBinding() {
        mBinding = ActivityBackgroundBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = mBinding.toolbar.toolbar;
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;

        if (!IzukoHelper.isHitagi()) {
            finish();
        }

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BackgroundAdapter();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
            setDocumentResultListener(uri -> {
                PageEntity pe = mAdapter.getItem(position);
                if (pe != null) {
                    pe.setBackgroundUri(uri.toString());
                    mAdapter.setData(position, pe);
                    AnywhereApplication.sRepository.updatePage(pe);
                }

            });
        });
        mBinding.rvList.setAdapter(mAdapter);

        List<PageEntity> pageEntityList = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        if (pageEntityList != null) {
            mAdapter.addData(pageEntityList);
        }

    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }
}

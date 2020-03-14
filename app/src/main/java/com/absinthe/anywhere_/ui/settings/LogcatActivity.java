package com.absinthe.anywhere_.ui.settings;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.log.LogAdapter;
import com.absinthe.anywhere_.databinding.ActivityLogcatBinding;
import com.absinthe.anywhere_.model.LogModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogcatActivity extends BaseActivity {

    private ActivityLogcatBinding mBinding;
    private LogAdapter mAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());

    @Override
    protected void setViewBinding() {
        mBinding = ActivityLogcatBinding.inflate(getLayoutInflater());
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
    protected void initView() {
        super.initView();

        mBinding.rvLog.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LogAdapter();
        mBinding.rvLog.setAdapter(mAdapter);

        initData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        List<LogModel> list = new ArrayList<>();

        File file = getExternalFilesDir(getString(R.string.logcat));
        if (file != null && file.exists()) {
            File[] fileList = file.listFiles();

            if (fileList != null) {
                for (File logFile : fileList) {
                    LogModel logModel = new LogModel();
                    String date = new Date(logFile.lastModified()).toLocaleString();
                    logModel.setCreateTime(date);
                    logModel.setFilePath(logFile.getAbsolutePath());
                    logModel.setFileSize(logFile.length());
                    list.add(logModel);
                }
                mAdapter.setNewData(list);
            }
        }
    }
}

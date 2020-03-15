package com.absinthe.anywhere_.ui.settings;

import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.log.LogAdapter;
import com.absinthe.anywhere_.adapter.log.LogDiffCallback;
import com.absinthe.anywhere_.databinding.ActivityLogcatBinding;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.LogModel;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.NotifyUtils;
import com.absinthe.anywhere_.utils.manager.LogRecorder;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.NotificationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogcatActivity extends BaseActivity {

    public static boolean isStartCatching = false;

    private ActivityLogcatBinding mBinding;
    private LogAdapter mAdapter;

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
    protected void onDestroy() {
        GlobalValues.sIsDebugMode = false;
        super.onDestroy();
    }

    @Override
    protected void initView() {
        super.initView();

        mBinding.rvLog.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LogAdapter();
        mAdapter.setDiffCallback(new LogDiffCallback());
        mBinding.rvLog.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            LogModel logModel = (LogModel) adapter.getItem(position);

            if (logModel != null) {
                if (view.getId() == R.id.btn_delete) {
                    if (FileUtils.delete(logModel.getFilePath())) {
                        mAdapter.remove(position);
                    }
                } else if (view.getId() == R.id.btn_send) {
                    File file = FileUtils.getFileByPath(logModel.getFilePath());
                    AppUtils.sendLogcat(this, file);
                }
            }
        });
        if (isStartCatching) {
            mBinding.btnLogcat.setText(getText((R.string.btn_stop_catch_log)));
        } else {
            mBinding.btnLogcat.setText(getText((R.string.btn_start_catch_log)));
        }
        mBinding.btnLogcat.setOnClickListener(v -> {
            if (isStartCatching) {
                mBinding.btnLogcat.setText(getText(R.string.btn_start_catch_log));
                isStartCatching = false;
                LogRecorder.getInstance().stop();
                NotificationUtils.cancel(NotifyUtils.LOGCAT_NOTIFICATION_ID);
                new Handler(Looper.getMainLooper()).postDelayed(() -> initData(true), 100);
            } else {
                mBinding.btnLogcat.setText(getText(R.string.btn_stop_catch_log));
                isStartCatching = true;
                AppUtils.startLogcat(LogcatActivity.this);
            }
        });

        initData(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(boolean isRefresh) {
        List<LogModel> list = new ArrayList<>();

        File file = getExternalFilesDir(getString(R.string.logcat));
        if (FileUtils.isFileExists(file)) {
            List<File> fileList = FileUtils.listFilesInDir(file, (o1, o2) ->
                    - String.valueOf(o1.lastModified()).compareTo(String.valueOf(o2.lastModified())));

            if (fileList != null) {
                for (File logFile : fileList) {
                    LogModel logModel = new LogModel();
                    String date = new Date(logFile.lastModified()).toLocaleString();
                    logModel.setCreateTime(date);
                    logModel.setFilePath(logFile.getAbsolutePath());
                    logModel.setFileSize(logFile.length());
                    list.add(logModel);
                }
                if (isRefresh) {
                    mAdapter.setDiffNewData(list);
                } else {
                    mAdapter.setNewData(list);
                }
            }
        }
    }
}

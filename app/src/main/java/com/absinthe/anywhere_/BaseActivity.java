package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.interfaces.OnDocumentResultListener;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.StatusBarUtil;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.Logger;

@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    private OnDocumentResultListener mListener;

    protected abstract void setViewBinding();
    protected abstract void setToolbar();
    protected abstract boolean isPaddingToolbar();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(this.getClass().getSimpleName(), "onCreate");
        setViewBinding();
        initView();
    }

    protected void initView() {
        if (GlobalValues.sBackgroundUri.isEmpty() || !(this instanceof MainActivity)) {
            if (UiUtils.isDarkMode(this)) {
                StatusBarUtil.setDarkMode(this);
            } else {
                StatusBarUtil.setLightMode(this);
            }
        }

        setToolbar();
        if (isPaddingToolbar()) {
            mToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        }
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void setDocumentResultListener(OnDocumentResultListener listener) {
        mListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mListener != null && data != null && data.getData() != null) {
                mListener.onResult(data.getData());
                AppUtils.takePersistableUriPermission(this, data.getData(), data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        if (GlobalValues.sIsExcludeFromRecent) {
            finishAndRemoveTask();
        } else {
            super.finish();
        }
    }
}

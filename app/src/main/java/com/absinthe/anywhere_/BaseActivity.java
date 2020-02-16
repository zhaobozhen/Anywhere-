package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.interfaces.OnDocumentResultListener;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.StatusBarUtil;
import com.absinthe.anywhere_.utils.UiUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private OnDocumentResultListener mListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(this.getClass().getSimpleName(), "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        int nav_color = getResources().getColor(R.color.navigationColorNormal);

        if ((GlobalValues.sBackgroundUri.isEmpty() && !GlobalValues.sIsPages) || !(this instanceof MainActivity)) {
            StatusBarUtil.setColorNoTranslucent(this, nav_color);
        }

        if (UiUtils.isDarkMode(this)) {
            if (GlobalValues.sBackgroundUri.isEmpty() || !(this instanceof MainActivity)) {
                UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
            } else {
                UiUtils.setActionBarTitle(this, getSupportActionBar());
            }
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

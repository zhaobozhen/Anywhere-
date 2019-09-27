package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.SPUtils;

public class MainActivity extends AppCompatActivity {
    private MainFragment mainFragment;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();

        mainFragment = MainFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commitNow();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String packageName = intent.getStringExtra(ConstUtil.INTENT_EXTRA_PACKAGE_NAME);
        String className = intent.getStringExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME);
        int classNameType = intent.getIntExtra(ConstUtil.INTENT_EXTRA_CLASS_NAME_TYPE, ConstUtil.SHORT_CLASS_NAME_TYPE);

        Log.d(TAG, "classNameType = " + classNameType);
        Log.d(TAG, "className = " + className);
        Log.d(TAG, "packageName = " + packageName);

        Bundle bundle = new Bundle();
        bundle.putString(ConstUtil.BUNDLE_PACKAGE_NAME, packageName);
        bundle.putString(ConstUtil.BUNDLE_CLASS_NAME, className);
        bundle.putInt(ConstUtil.BUNDLE_CLASS_NAME_TYPE, classNameType);

        mainFragment.setArguments(bundle);
    }

    private void initView() {
        if (!SPUtils.getString(this, ConstUtil.SP_KEY_CHANGE_BACKGROUND).isEmpty()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            }
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.transparent));
        }
    }
}

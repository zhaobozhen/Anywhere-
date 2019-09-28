package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.ImageUtils;
import com.absinthe.anywhere_.utils.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

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
        String backgroundUri = SPUtils.getString(this, ConstUtil.SP_KEY_CHANGE_BACKGROUND);
        ImageView ivBackground = findViewById(R.id.iv_background);
        if (!backgroundUri.isEmpty()) {
            ImageUtils.setActionBarStyle(this, ImageUtils.ACTION_BAR_TRANSLATE);
            ImageUtils.loadBackgroundPic(this, ivBackground);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView ivBackground = findViewById(R.id.iv_background);

        ImageUtils.loadBackgroundPic(this, ivBackground);
    }
}

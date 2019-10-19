package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.UiUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainFragment mainFragment;
    private static Fragment curFragment;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
        instance = this;

        if (GlobalValues.sIsFirstLaunch) {
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.container, welcomeFragment)
                    .commitNow();
        } else {
            mainFragment = MainFragment.newInstance();
            getAnywhereIntent(getIntent());

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container, mainFragment)
                        .commitNow();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnywhereApplication.setTheme(GlobalValues.sDarkMode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getAnywhereIntent(intent);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivBackground = findViewById(R.id.iv_background);
        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            UiUtils.setActionBarTransparent(this);
            UiUtils.loadBackgroundPic(this, ivBackground);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView ivBackground = findViewById(R.id.iv_background);

        UiUtils.loadBackgroundPic(this, ivBackground);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: actionBarType = " + GlobalValues.sActionBarType);

        if (menu.findItem(R.id.toolbar_settings) != null) {
            switch (GlobalValues.sActionBarType) {
                case "":
                case Const.ACTION_BAR_TYPE_LIGHT:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_light);
                    menu.findItem(R.id.toolbar_sort).setIcon(R.drawable.ic_filter_list_light);
                    break;
                case Const.ACTION_BAR_TYPE_DARK:
                    menu.findItem(R.id.toolbar_settings).setIcon(R.drawable.ic_settings_outline_dark);
                    menu.findItem(R.id.toolbar_sort).setIcon(R.drawable.ic_filter_list_dark);
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void setCurFragment(Fragment fragment) {
        curFragment = fragment;
    }

    public void setMainFragment(MainFragment fragment) {
        mainFragment = fragment;
    }

    private void getAnywhereIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        } else {
            Log.d(TAG, "Received Url = " + uri.toString());
        }
        String scheme = uri.getScheme();
        String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
        String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
        String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);
        Log.d(TAG, "Url param = "+param1+", "+param2+", "+param3);

        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_EXTRA_PARAM_1, param1);
        bundle.putString(Const.INTENT_EXTRA_PARAM_2, param2);
        bundle.putString(Const.INTENT_EXTRA_PARAM_3, param3);

        mainFragment.setArguments(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "curFragment = " + curFragment);

        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Log.d(TAG, "REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION");
            if (curFragment instanceof MainFragment) {
                if (mainFragment == null) {
                    mainFragment = (MainFragment) curFragment;
                }
                if (resultCode == RESULT_OK) {
                    mainFragment.checkWorkingPermission();
                }
            } else if (curFragment instanceof InitializeFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        InitializeFragment.getViewModel().getIsOverlay().setValue(Boolean.TRUE);
                    }
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            Log.d(TAG, "REQUEST_CODE_SHIZUKU_PERMISSION");
            if (curFragment instanceof InitializeFragment) {
                InitializeFragment.getViewModel().getIsShizuku().setValue(Boolean.TRUE);
            }
        }
    }
}

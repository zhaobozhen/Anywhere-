package com.absinthe.anywhere_.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.LogUtil;

public class SettingsActivity extends BaseActivity {
    private static SettingsActivity instance;

    public static SettingsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, SettingsFragment.newInstance())
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.REQUEST_CODE_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri backgroundUri = data.getData();
                        if (backgroundUri != null) {
                            LogUtil.d("backgroundUri = " + backgroundUri);
                            GlobalValues.setsBackgroundUri(backgroundUri.toString());
                            GlobalValues.setsActionBarType("");
                            if (MainFragment.getViewModelInstance() != null) {
                                MainFragment.getViewModelInstance().getBackground().setValue(backgroundUri.toString());
                                MainActivity.getInstance().restartActivity();
                                finish();
                            }
                        }
                    } else {
                        LogUtil.d("onActivityResult: REQUEST_CODE_IMAGE_CAPTURE: data = null.");
                    }
                }
                break;
            case Const.REQUEST_CODE_PHOTO_CROP:
                break;
            default:
        }
    }
}

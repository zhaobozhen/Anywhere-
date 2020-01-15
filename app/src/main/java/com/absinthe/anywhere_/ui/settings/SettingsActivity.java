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
import com.absinthe.anywhere_.utils.manager.Logger;

public class SettingsActivity extends BaseActivity {
    private static SettingsActivity sInstance;

    public static SettingsActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sInstance = this;

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
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
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
                            Logger.d("backgroundUri = " + backgroundUri);
                            GlobalValues.setsBackgroundUri(backgroundUri.toString());
                            GlobalValues.setsActionBarType("");
                            if (MainActivity.getInstance().getViewModel() != null) {
                                MainActivity.getInstance().getViewModel().getBackground().setValue(backgroundUri.toString());
                                MainActivity.getInstance().restartActivity();
                                finish();
                            }
                        }
                    } else {
                        Logger.d("onActivityResult: REQUEST_CODE_IMAGE_CAPTURE: data = null.");
                    }
                }
                break;
            case Const.REQUEST_CODE_PHOTO_CROP:
                break;
            default:
        }
    }
}

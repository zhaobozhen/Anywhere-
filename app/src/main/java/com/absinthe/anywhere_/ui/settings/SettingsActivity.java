package com.absinthe.anywhere_.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.LogUtil;

public class SettingsActivity extends AppCompatActivity {
    private static SettingsActivity instance;

    public static SettingsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        instance = this;

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
                            LogUtil.d(this.getClass(), "backgroundUri = " + backgroundUri);
                            GlobalValues.setsBackgroundUri(backgroundUri.toString());
                            GlobalValues.setsActionBarType("");
                            if (MainFragment.getViewModelInstance() != null) {
                                MainFragment.getViewModelInstance().getBackground().setValue(backgroundUri.toString());
                            }
                        }
                    } else {
                        LogUtil.d(this.getClass(), "onActivityResult: REQUEST_CODE_IMAGE_CAPTURE: data = null.");
                    }
                }
                break;
            case Const.REQUEST_CODE_PHOTO_CROP:
                break;
            default:
        }
    }
}

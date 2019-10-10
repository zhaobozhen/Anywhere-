package com.absinthe.anywhere_.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.SPUtils;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
            case ConstUtil.REQUEST_CODE_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri backgroundUri = data.getData();
                        if (backgroundUri != null) {
                            Log.d(TAG, "backgroundUri = " + backgroundUri);
                            SPUtils.putString(this, ConstUtil.SP_KEY_CHANGE_BACKGROUND, backgroundUri.toString());
                            SPUtils.putString(this, ConstUtil.SP_KEY_ACTION_BAR_TYPE, "");
                            MainFragment.getViewModelInstance().getBackground().setValue(backgroundUri.toString());
                        }
                    } else {
                        Log.d(TAG, "onActivityResult: REQUEST_CODE_IMAGE_CAPTURE: data = null.");
                    }
                }
                break;
            case ConstUtil.REQUEST_CODE_PHOTO_CROP:
                break;
            default:
        }
    }
}

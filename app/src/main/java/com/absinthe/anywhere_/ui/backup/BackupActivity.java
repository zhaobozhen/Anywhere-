package com.absinthe.anywhere_.ui.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class BackupActivity extends BaseActivity {
    private static BackupActivity sInstance;

    public static BackupActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
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
                    .replace(R.id.settings_container, BackupFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE_WRITE_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    OutputStream os = getContentResolver().openOutputStream(
                            Objects.requireNonNull(
                                    data.getData()));
                    if (os != null) {
                        String content = StorageUtils.ExportAnywhereEntityJsonString();
                        String encrypted = CipherUtils.encrypt(content);
                        if (encrypted != null) {
                            os.write(encrypted.getBytes());
                        }
                        os.close();
                        ToastUtil.makeText(getString(R.string.toast_backup_success));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_RESTORE_BACKUPS && resultCode == RESULT_OK) {
            if (data != null) {
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    BufferedReader reader;
                    if (inputStream != null) {
                        reader = new BufferedReader(new InputStreamReader(
                                inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        String content = CipherUtils.decrypt(stringBuilder.toString());
                        Logger.d(content);

                        Gson gson = new Gson();
                        List<AnywhereEntity> list = gson.fromJson(content,
                                new TypeToken<List<AnywhereEntity>>() {
                                }.getType());

                        if (list != null) {
                            for (AnywhereEntity ae : list) {
                                MainFragment.getViewModelInstance().insert(ae);
                            }
                            ToastUtil.makeText(getString(R.string.toast_restore_success));
                        }

                        inputStream.close();
                        reader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

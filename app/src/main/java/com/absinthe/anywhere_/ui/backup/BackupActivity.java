package com.absinthe.anywhere_.ui.backup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.settings.SettingsFragment;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

public class BackupActivity extends AppCompatActivity {
    private static BackupActivity instance;

    public static BackupActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        instance = this;

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
                    if( os != null ) {
                        String content = StorageUtils.ExportAnywhereEntityJsonString();
                        String encrypted = CipherUtils.encrypt(content);
                        if (encrypted != null) {
                            os.write(encrypted.getBytes());
                        }
                        os.close();
                        ToastUtil.makeText("备份成功");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_RESTORE_BACKUPS && resultCode == RESULT_OK) {
            if (data != null) {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    BufferedReader reader = null;
                    if (inputStream != null) {
                        reader = new BufferedReader(new InputStreamReader(
                                inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        String content = CipherUtils.decrypt(stringBuilder.toString());
                        LogUtil.d(BackupActivity.class, stringBuilder.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

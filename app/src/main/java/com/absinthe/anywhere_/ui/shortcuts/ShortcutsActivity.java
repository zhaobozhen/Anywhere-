package com.absinthe.anywhere_.ui.shortcuts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.EditUtils;

public class ShortcutsActivity extends AppCompatActivity {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();

        if (action != null) {
            if (action.equals(ACTION_START_COLLECTOR)) {
                if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("shortcutEditUrl", "true");
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, CollectorService.class);
                    intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
                    startService(intent);
                    finish();
                }
            }
        }
    }
}

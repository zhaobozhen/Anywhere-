package com.absinthe.anywhere_.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;

public class BackgroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!IzukoHelper.isHitagi()) {
            finish();
        }

        setContentView(R.layout.activity_background);
    }
}

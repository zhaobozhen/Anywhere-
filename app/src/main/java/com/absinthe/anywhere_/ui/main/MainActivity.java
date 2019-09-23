package com.absinthe.anywhere_.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ConstUtil;

public class MainActivity extends AppCompatActivity {
    private MainFragment mainFragment;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Log.d(TAG,"onCreate");

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
        String packageName = intent.getStringExtra("packageName");
        String className = intent.getStringExtra("className");
        int classNameType = intent.getIntExtra("classNameType", ConstUtil.SHORT_CLASS_NAME_TYPE);

        Log.d(TAG, "classNameType = " + classNameType);
        Log.d(TAG, "className = " + className);
        Log.d(TAG, "packageName = " + packageName);

        Bundle bundle = new Bundle();
        bundle.putString("packageName", packageName);
        bundle.putString("className", className);
        bundle.putInt("classNameType", classNameType);

        mainFragment.setArguments(bundle);
    }
}

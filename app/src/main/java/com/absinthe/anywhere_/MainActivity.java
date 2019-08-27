package com.absinthe.anywhere_;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.absinthe.anywhere_.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main","onCreate");
        setContentView(R.layout.main_activity);
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

        Bundle bundle = new Bundle();
        bundle.putString("packageName", packageName);
        bundle.putString("className", className);

        mainFragment.setArguments(bundle);
    }
}

package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.URLManager;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GiftActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!BuildConfig.DEBUG) {
            finish();
        }

        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");

        RequestBody requestBody  = RequestBody.create("{code='00000-00000-00000-00000'}", JSON);

        Request request = new Request.Builder()
                .url(URLManager.GIFT_SCF_URL)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            Response response;
            try {
                response = client.newCall(request).execute();
                ToastUtil.makeText(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

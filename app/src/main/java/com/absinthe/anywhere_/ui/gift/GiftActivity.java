package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

        FormBody formBody = new FormBody.Builder()
                .add("code", "00000-00000-00000-00000")
                .build();

        Request request = new Request.Builder()
                .url(URLManager.GIFT_SCF_URL)
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    GiftModel giftModel = new Gson().fromJson(result, GiftModel.class);
                    if (giftModel != null) {
                        if (giftModel.getIsActive() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.makeText("Active!");
                                }
                            });
                        }
                    }
                    Logger.d(result);
                    response.body().close();
                } else {
                    Logger.d("Failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}

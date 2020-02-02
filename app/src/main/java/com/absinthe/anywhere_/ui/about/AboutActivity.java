package com.absinthe.anywhere_.ui.about;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.drakeet.about.AbsAboutActivity;
import com.drakeet.about.Card;
import com.drakeet.about.Category;
import com.drakeet.about.Contributor;
import com.drakeet.about.License;
import com.drakeet.about.OnRecommendationClickedListener;
import com.drakeet.about.Recommendation;
import com.drakeet.about.extension.RecommendationLoaderDelegate;
import com.drakeet.about.extension.provided.GsonJsonConverter;
import com.drakeet.about.provided.GlideImageLoader;

import java.util.List;

public class AboutActivity extends AbsAboutActivity implements OnRecommendationClickedListener {
    private int mClickCount;
    private long mStartTime, mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setImageLoader(new GlideImageLoader());
        setOnRecommendationClickedListener(this);
    }

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        View.OnClickListener listener = createDebugListener();
        icon.setImageResource(R.drawable.pic_splash);
        icon.setOnClickListener(listener);
        slogan.setText(getString(R.string.slogan));
        version.setText(String.format("Version: %s", BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onItemsCreated(@NonNull List<Object> items) {
        items.add(new Category(getString(R.string.whats_this)));
        items.add(new Card(getString(R.string.about_text)));

        items.add(new Category(getString(R.string.developer)));
        items.add(new Contributor(R.mipmap.pic_rabbit, "Absinthe", getString(R.string.developer_info), "https://www.coolapk.com/u/482045"));

        items.add(new Category(getString(R.string.certification)));
        items.add(new Contributor(R.mipmap.pic_android_links, getString(R.string.android_links_title), "https://androidlinks.org/", "https://androidlinks.org/"));
        items.add(new Contributor(R.drawable.ic_green_android, getString(R.string.green_android_title), "https://green-android.org/", "https://green-android.org/"));

        items.add(new Category(getString(R.string.open_source_licenses)));
        items.add(new License("Shizuku", "Rikka", "License", "https://github.com/RikkaApps/Shizuku"));
        items.add(new License("FreeReflection", "tiann", License.MIT, "https://github.com/tiann/FreeReflection"));
        items.add(new License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("Material Tap Target Prompt", "sjwall", License.APACHE_2, "https://github.com/sjwall/MaterialTapTargetPrompt"));
        items.add(new License("FloatingActionButtonSpeedDial", "leinardi", License.APACHE_2, "https://github.com/leinardi/FloatingActionButtonSpeedDial"));
        items.add(new License("glide", "bumptech", "License", "https://github.com/bumptech/glide"));
        items.add(new License("AndResGuard", "shwenzhang", License.APACHE_2, "https://github.com/shwenzhang/AndResGuard"));
        items.add(new License("IceBox-SDK", "heruoxin", License.APACHE_2, "https://github.com/heruoxin/IceBox-SDK"));
        items.add(new License("Robfuscate", "heruoxin", License.APACHE_2, "https://github.com/heruoxin/Robfuscate"));
        items.add(new License("Once", "jonfinerty", License.APACHE_2, "https://github.com/jonfinerty/Once"));
        items.add(new License("BaseRecyclerViewAdapterHelper", "CymChad", License.MIT, "https://github.com/CymChad/BaseRecyclerViewAdapterHelper"));
        items.add(new License("colorpicker", "QuadFlask", License.APACHE_2, "https://github.com/QuadFlask/colorpicker"));
        items.add(new License("gson", "Google", License.APACHE_2, "https://github.com/google/gson"));
        items.add(new License("AndroidX", "Google", License.APACHE_2, "https://source.google.com"));
        items.add(new License("Android Jetpack", "Google", License.APACHE_2, "https://source.google.com"));
        items.add(new License("Palette", "Google", License.APACHE_2, "https://source.google.com"));
        items.add(new License("OkHttp", "Square", License.APACHE_2, "https://github.com/square/okhttp"));
        items.add(new License("Retrofit", "Square", License.APACHE_2, "https://github.com/square/retrofit"));
        items.add(new License("LeakCanary", "Square", License.APACHE_2, "https://github.com/square/leakcanary"));
        items.add(new License("RxAndroid", "JakeWharton", License.APACHE_2, "https://github.com/ReactiveX/RxAndroid"));
        items.add(new License("RxJava", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxJava"));

        RecommendationLoaderDelegate.attach(this, items.size(), new GsonJsonConverter());
    }

    private View.OnClickListener createDebugListener() {
        mClickCount = 0;
        mStartTime = mEndTime = 0;

        return view -> {
            mEndTime = System.currentTimeMillis();
            if (mEndTime - mStartTime > 500) {
                mClickCount = 0;
            } else {
                mClickCount++;
            }
            mStartTime = mEndTime;

            if (mClickCount == 9) {
                GlobalValues.sIsDebugMode = true;
                DialogManager.showDebugDialog(this);
            }
        };
    }

    @Override
    public boolean onRecommendationClicked(@NonNull View itemView, @NonNull Recommendation recommendation) {
        return false;
    }

    private void initView() {
        getWindow().setNavigationBarColor(getResources().getColor(R.color.transparent));
        if (UiUtils.isDarkMode(this)) {
            UiUtils.clearLightStatusBarAndNavigationBar(getWindow().getDecorView());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.toolbar_rate) {
            try {
                URLSchemeHandler.parse(URLManager.MARKET_URL_SCHEME, this);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}


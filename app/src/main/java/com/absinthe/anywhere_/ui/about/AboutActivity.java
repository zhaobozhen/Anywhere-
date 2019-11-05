package com.absinthe.anywhere_.ui.about;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.drakeet.about.AbsAboutActivity;
import com.drakeet.about.Card;
import com.drakeet.about.Category;
import com.drakeet.about.Contributor;
import com.drakeet.about.License;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AboutActivity extends AbsAboutActivity {
    private int mClickCount;
    private long mStartTime, mEndTime;

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
        items.add(new Category("这是什么"));
        items.add(new Card(getString(R.string.about_text)));

        items.add(new Category("Developers"));
        items.add(new Contributor(R.drawable.pic_rabbit, "Absinthe", "Developer & designer", "coolmarket://www.coolapk.com/u/482045"));

        items.add(new Category("Open Source Licenses"));
        items.add(new License("Shizuku", "Rikka", "License", "https://github.com/RikkaApps/Shizuku"));
        items.add(new License("FreeReflection", "tiann", License.MIT, "https://github.com/tiann/FreeReflection"));
        items.add(new License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("IceBox-SDK", "heruoxin", License.APACHE_2, "https://github.com/heruoxin/IceBox-SDK"));
        items.add(new License("Material Tap Target Prompt", "sjwall", License.APACHE_2, "https://github.com/sjwall/MaterialTapTargetPrompt"));
        items.add(new License("FloatingActionButtonSpeedDial", "leinardi", License.APACHE_2, "https://github.com/leinardi/FloatingActionButtonSpeedDial"));
        items.add(new License("Glide", "bumptech", "License", "https://github.com/bumptech/glide"));
        items.add(new License("Gson", "Google", License.APACHE_2, "https://github.com/google/gson"));
        items.add(new License("AndroidX", "Google", License.APACHE_2, "https://source.google.com"));
        items.add(new License("Android Jetpack", "Google", License.APACHE_2, "https://source.google.com"));
        items.add(new License("Palette", "Google", License.APACHE_2, "https://source.google.com"));

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
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Debug info")
                        .setMessage(Html.fromHtml("<b>workingMode</b> = " + GlobalValues.sWorkingMode + "<br>"
                                + "<b>backgroundUri</b> = " + GlobalValues.sBackgroundUri + "<br>"
                                + "<b>actionBarType</b> = " + GlobalValues.sActionBarType + "<br>"
                                + "<b>sortMode</b> = " + GlobalValues.sSortMode + "<br>"))
                        .setPositiveButton(R.string.dialog_delete_positive_button, null)
                        .setCancelable(false)
                        .show();
            }
        };
    }
}


package com.absinthe.anywhere_.ui.about;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.drakeet.about.AbsAboutActivity;

import java.util.List;

public class AboutActivity extends AbsAboutActivity {

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.drawable.splash);
        slogan.setText(getString(R.string.app_name));
        version.setText(String.format("Version:%s", BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onItemsCreated(@NonNull List<Object> items) {

    }
}

package com.absinthe.anywhere_.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;

import java.util.ArrayList;
import java.util.List;

public class ShortcutsUtil {
    private static ShortcutManager mShortcutManager;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static ShortcutManager getInstance() {
        if (mShortcutManager == null) {
            mShortcutManager = AnywhereApplication.sContext.getSystemService(ShortcutManager.class);
        }
        return mShortcutManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void addShortcut(AnywhereEntity ae) {
        Intent intent = new Intent(AnywhereApplication.sContext, ShortcutsActivity.class);
        intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
        intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, TextUtils.getItemCommand(ae));

        List<ShortcutInfo> infos = new ArrayList<>();
        ShortcutInfo info = new ShortcutInfo.Builder(AnywhereApplication.sContext, ae.getTimeStamp())
                .setShortLabel(ae.getAppName())
                .setIcon(Icon.createWithBitmap(UIUtils.drawableToBitmap(UIUtils.getAppIconByPackageName(AnywhereApplication.sContext, ae))))
                .setIntent(intent)
                .build();
        infos.add(info);
        getInstance().addDynamicShortcuts(infos);

        AnywhereEntity item = new AnywhereEntity(ae.getId(), ae.getAppName(), ae.getParam1(), ae.getParam2(), ae.getParam3(), ae.getDescription(), ae.getAnywhereType() + 10, ae.getTimeStamp());
        MainFragment.getViewModelInstance().update(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void removeShortcut(AnywhereEntity ae) {
        if (ae == null) {
            return;
        }

        AnywhereEntity item = new AnywhereEntity(ae.getId(), ae.getAppName(), ae.getParam1(), ae.getParam2(), ae.getParam3(), ae.getDescription(), ae.getAnywhereType(), ae.getTimeStamp());
        MainFragment.getViewModelInstance().update(item);

        List<String> shortcutsIds = new ArrayList<>();
        shortcutsIds.add(ae.getTimeStamp());
        getInstance().removeDynamicShortcuts(shortcutsIds);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addPinnedShortcut(AnywhereEntity ae) {
        if (getInstance().isRequestPinShortcutSupported()) {
            // Assumes there's already a shortcut with the ID "my-shortcut".
            // The shortcut must be enabled.
            Intent intent = new Intent(AnywhereApplication.sContext, ShortcutsActivity.class);
            intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, TextUtils.getItemCommand(ae));

            ShortcutInfo pinShortcutInfo =
                    new ShortcutInfo.Builder(MainActivity.getInstance(), ae.getId())
                            .setShortLabel(ae.getAppName())
                            .setIcon(Icon.createWithBitmap(UIUtils.drawableToBitmap(UIUtils.getAppIconByPackageName(AnywhereApplication.sContext, ae))))
                            .setIntent(intent)
                            .build();

            // Create the PendingIntent object only if your app needs to be notified
            // that the user allowed the shortcut to be pinned. Note that, if the
            // pinning operation fails, your app isn't notified. We assume here that the
            // app has implemented a method called createShortcutResultIntent() that
            // returns a broadcast intent.
            Intent pinnedShortcutCallbackIntent =
                    getInstance().createShortcutResultIntent(pinShortcutInfo);

            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully.For details, see PendingIntent.getBroadcast().
            PendingIntent successCallback = PendingIntent.getBroadcast(MainActivity.getInstance(), /* request code */ 0,
                    pinnedShortcutCallbackIntent, /* flags */ 0);

            getInstance().requestPinShortcut(pinShortcutInfo,
                    successCallback.getIntentSender());

            ToastUtil.makeText(R.string.toast_try_to_add_pinned_shortcut);
        }
    }
}

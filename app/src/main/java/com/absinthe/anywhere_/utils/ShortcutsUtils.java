package com.absinthe.anywhere_.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.database.AnywhereEntity;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShortcutsUtils {

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public enum Singleton {
        INSTANCE;
        private ShortcutManager instance;

        Singleton() {
            instance = Utils.getApp().getSystemService(ShortcutManager.class);
        }

        public ShortcutManager getInstance() {
            return instance;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void addShortcut(AnywhereEntity ae) {
        Intent intent = new Intent(Utils.getApp(), ShortcutsActivity.class);
        if (ae.getAnywhereType() == AnywhereType.QR_CODE) {
            intent.setAction(ShortcutsActivity.ACTION_START_QR_CODE);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, AnywhereType.QRCODE_PREFIX + ae.getParam2());
        } else if (ae.getAnywhereType() == AnywhereType.IMAGE) {
            intent.setAction(ShortcutsActivity.ACTION_START_IMAGE);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, ae.getParam1());
        } else {
            intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, TextUtils.getItemCommand(ae));
        }

        List<ShortcutInfo> infos = new ArrayList<>();
        ShortcutInfo info = new ShortcutInfo.Builder(Utils.getApp(), ae.getId())
                .setShortLabel(ae.getAppName())
                .setIcon(Icon.createWithBitmap(ConvertUtils.drawable2Bitmap(UiUtils.getAppIconByPackageName(Utils.getApp(), ae))))
                .setIntent(intent)
                .build();
        infos.add(info);
        if (Singleton.INSTANCE.getInstance().getDynamicShortcuts().size() <= 3) {
            Singleton.INSTANCE.getInstance().addDynamicShortcuts(infos);
        }

        AnywhereEntity item = new AnywhereEntity(ae);
        item.setType(item.getExportedType() * 100 + 10 + item.getAnywhereType());
        AnywhereApplication.sRepository.update(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void updateShortcut(AnywhereEntity ae) {
        Intent intent = new Intent(Utils.getApp(), ShortcutsActivity.class);
        if (ae.getAnywhereType() == AnywhereType.QR_CODE) {
            intent.setAction(ShortcutsActivity.ACTION_START_QR_CODE);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, AnywhereType.QRCODE_PREFIX + ae.getParam2());
        } else if (ae.getAnywhereType() == AnywhereType.IMAGE) {
            intent.setAction(ShortcutsActivity.ACTION_START_IMAGE);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, ae.getParam1());
        } else {
            intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
            intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, TextUtils.getItemCommand(ae));
        }

        List<ShortcutInfo> infos = new ArrayList<>();
        ShortcutInfo info = new ShortcutInfo.Builder(Utils.getApp(), ae.getId())
                .setShortLabel(ae.getAppName())
                .setIcon(Icon.createWithBitmap(ConvertUtils.drawable2Bitmap(UiUtils.getAppIconByPackageName(Utils.getApp(), ae))))
                .setIntent(intent)
                .build();
        infos.add(info);

        Singleton.INSTANCE.getInstance().updateShortcuts(infos);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void removeShortcut(AnywhereEntity ae) {
        if (ae == null) {
            return;
        }

        AnywhereEntity item = new AnywhereEntity(ae);
        item.setType(item.getExportedType() * 100 + item.getAnywhereType());
        AnywhereApplication.sRepository.update(item);

        List<String> shortcutsIds = new ArrayList<>();
        shortcutsIds.add(ae.getId());
        Singleton.INSTANCE.getInstance().removeDynamicShortcuts(shortcutsIds);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addPinnedShortcut(AnywhereEntity ae, Drawable icon, String name) {
        if (Singleton.INSTANCE.getInstance().isRequestPinShortcutSupported()) {
            // Assumes there's already a shortcut with the ID "my-shortcut".
            // The shortcut must be enabled.
            Intent intent = new Intent(Utils.getApp(), ShortcutsActivity.class);
            if (ae.getAnywhereType() == AnywhereType.IMAGE) {
                intent.setAction(ShortcutsActivity.ACTION_START_IMAGE);
                intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, ae.getParam1());
            } else {
                intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
                intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, TextUtils.getItemCommand(ae));
            }

            ShortcutInfo pinShortcutInfo =
                    new ShortcutInfo.Builder(Utils.getApp(), ae.getId())
                            .setShortLabel(name)
                            .setIcon(Icon.createWithBitmap(ConvertUtils.drawable2Bitmap(icon)))
                            .setIntent(intent)
                            .build();

            // Create the PendingIntent object only if your app needs to be notified
            // that the user allowed the shortcut to be pinned. Note that, if the
            // pinning operation fails, your app isn't notified. We assume here that the
            // app has implemented a method called createShortcutResultIntent() that
            // returns a broadcast intent.
            Intent pinnedShortcutCallbackIntent =
                    Singleton.INSTANCE.getInstance().createShortcutResultIntent(pinShortcutInfo);

            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully.For details, see PendingIntent.getBroadcast().
            PendingIntent successCallback = PendingIntent.getBroadcast(Utils.getApp(), /* request code */ 0,
                    pinnedShortcutCallbackIntent, /* flags */ 0);

            Singleton.INSTANCE.getInstance().requestPinShortcut(pinShortcutInfo,
                    successCallback.getIntentSender());

            ToastUtil.makeText(R.string.toast_try_to_add_pinned_shortcut);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void clearShortcuts() {
        Singleton.INSTANCE.getInstance().removeAllDynamicShortcuts();

        new Thread(() -> {
            List<AnywhereEntity> items = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
            if (items != null) {
                for (int iter = 0, len = items.size(); iter < len; iter++) {
                    AnywhereEntity item = items.get(iter);
                    item.setType(item.getAnywhereType());
                    AnywhereApplication.sRepository.update(item);
                }
            }
        }).start();
    }
}

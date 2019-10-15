package com.absinthe.anywhere_.utils;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsUtil {
    private static ShortcutManager mShortcutManager;

    public static ShortcutManager getInstance() {
        if (mShortcutManager == null) {
            mShortcutManager = AnywhereApplication.sContext.getSystemService(ShortcutManager.class);
        }
        return mShortcutManager;
    }

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
}

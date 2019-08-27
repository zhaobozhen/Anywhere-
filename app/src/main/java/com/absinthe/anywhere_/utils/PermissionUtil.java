package com.absinthe.anywhere_.utils;

import android.content.Intent;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";
    public static void goToMIUIPermissionManager() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("extra_pkgname", "com.absinthe.anywhere_");
    }
}

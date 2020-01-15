package com.absinthe.anywhere_.utils;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.provider.HomeWidgetProvider;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    /**
     * react the url scheme
     *
     * @param context to launch an intent
     * @param param1  param1
     * @param param2  param2
     * @param param3  param3
     */
    public static void openUrl(Context context, String param1, String param2, String param3) {
        String url = "anywhere://url?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3;
        URLSchemeHandler.parse(url, context);
    }

    /**
     * Update Anywhere- widget
     *
     * @param context context
     */
    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, HomeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context,
                        HomeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    /**
     * Judge that whether an app is frost
     *
     * @param context context
     * @param item    Anywhere- entity
     * @return true if the app is frost
     */
    public static boolean isAppFrozen(Context context, AnywhereEntity item) {
        int type = item.getAnywhereType();
        String apkTempPackageName;

        if (type == AnywhereType.URL_SCHEME) {
            if (android.text.TextUtils.isEmpty(item.getParam2())) {
                apkTempPackageName = UiUtils.getPkgNameByUrl(context, item.getParam1());
            } else {
                apkTempPackageName = item.getParam2();
            }

            try {
                return IceBox.getAppEnabledSetting(context, apkTempPackageName) != 0;   //0 为未冻结状态
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else if (type == AnywhereType.ACTIVITY) {
            try {
                return IceBox.getAppEnabledSetting(context, item.getParam1()) != 0;   //0 为未冻结状态
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Get apps list
     *
     * @param packageManager android package manager
     * @param showSystem     true if show system apps
     * @return apps list
     */
    public static List<AppListBean> getAppList(PackageManager packageManager, boolean showSystem) {
        List<AppListBean> list = new ArrayList<>();

        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0, len = packageInfos.size(); i < len; i++) {
                PackageInfo packageInfo = packageInfos.get(i);

                //Filter system apps
                if (!showSystem) {
                    if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                        continue;
                    }
                }

                AppListBean bean = new AppListBean();
                bean.setPackageName(packageInfo.packageName);
                bean.setAppName(TextUtils.getAppName(AnywhereApplication.sContext, packageInfo.packageName));

                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }

                Drawable icon;
                if (GlobalValues.sIconPack.equals(Settings.DEFAULT_ICON_PACK) || GlobalValues.sIconPack.isEmpty()) {
                    icon = packageInfo.applicationInfo.loadIcon(packageManager);
                } else {
                    icon = Settings.sIconPack.getDrawableIconForPackage(packageInfo.packageName, packageInfo.applicationInfo.loadIcon(packageManager));
                }
                bean.setIcon(icon);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ListUtils.sortAppListByNameAsc(list);
    }

    /**
     * get all activities of an app
     *
     * @param context     context
     * @param packageName package name of the app
     * @return activities list
     */
    public static List<String> getActivitiesClass(Context context, String packageName) {
        List<String> returnClassList = new ArrayList<>();

        try {
            //Get all activity classes in the AndroidManifest.xml
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            if (packageInfo.activities != null) {
                Logger.d(AppUtils.class, "Found ", packageInfo.activities.length, " activity in the AndroidManifest.xml");

                for (ActivityInfo ai : packageInfo.activities) {
                    returnClassList.add(ai.name);
                    Logger.d(AppUtils.class, ai.name, "...OK");
                }

            }
        } catch (PackageManager.NameNotFoundException | RuntimeException exception) {
            exception.printStackTrace();
        }

        return returnClassList;
    }

    public static boolean isServiceRunning(Context context, String clazz) {
        if (android.text.TextUtils.isEmpty(clazz)) {
            return false;
        }

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfos =
                    (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(30);
            for (ActivityManager.RunningServiceInfo info : runningServiceInfos) {
                if (info.service.getClassName().equals(clazz)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}

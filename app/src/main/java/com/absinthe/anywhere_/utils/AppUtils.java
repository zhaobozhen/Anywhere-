package com.absinthe.anywhere_.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.provider.HomeWidgetProvider;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppUtils {
    /**
     * react the url scheme
     * @param context to launch an intent
     * @param param1 param1
     * @param param2 param2
     * @param param3 param3
     */
    public static void openUrl(Context context, String param1, String param2, String param3) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String url = "anywhere://url?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3;
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

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

    public static boolean isAppFrozen(Context context, AnywhereEntity item) {
        int type = item.getAnywhereType();
        String apkTempPackageName = "";

        if (type == AnywhereType.URL_SCHEME) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(item.getParam1()));
            List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo.size() != 0) {
                apkTempPackageName = resolveInfo.get(0).activityInfo.packageName;
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

    public static List<AppListBean> getAppList(PackageManager packageManager, boolean showSystem) {
        List<AppListBean> list = new ArrayList<>();

        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);

                //过滤掉系统app
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
                bean.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ListUtils.sortAppListByNameAsc(list);
    }

    public static List<String> getActivitiesClass(Context context, String packageName){
        List<String> returnClassList = new ArrayList<>();

        try {
            //Get all activity classes in the AndroidManifest.xml
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            if (packageInfo.activities != null) {
                LogUtil.d(AppUtils.class, "Found ", packageInfo.activities.length, " activity in the AndroidManifest.xml");

                for (ActivityInfo ai : packageInfo.activities) {
                    returnClassList.add(ai.name);
                    LogUtil.d(AppUtils.class, ai.name, "...OK");
                }

                LogUtil.d(AppUtils.class, "Return ", returnClassList.size(), " activity,", Arrays.toString(returnClassList.toArray()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return returnClassList;
    }
}

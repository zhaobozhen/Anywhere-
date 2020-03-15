package com.absinthe.anywhere_.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;

import androidx.core.content.FileProvider;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.receiver.HomeWidgetProvider;
import com.absinthe.anywhere_.ui.settings.LogcatActivity;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.LogRecorder;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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
        String url = URLManager.ANYWHERE_SCHEME + URLManager.URL_HOST + "?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3;
        URLSchemeHandler.parse(url, context);
    }

    public static void openNewURLScheme(Context context) {
        String url = URLManager.ANYWHERE_SCHEME + URLManager.URL_HOST + "?param1=&param2=&param3=";
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
                Timber.d("Found %d activity in the AndroidManifest.xml", packageInfo.activities.length);

                for (ActivityInfo ai : packageInfo.activities) {
                    returnClassList.add(ai.name);
                    Timber.d(ai.name, "...OK");
                }

            }
        } catch (PackageManager.NameNotFoundException | RuntimeException exception) {
            exception.printStackTrace();
        }

        return returnClassList;
    }

    /**
     * Get device's Android ID
     *
     * @param context Context
     * @return Android ID
     */
    public static String getAndroidId(Context context) {
        return android.provider.Settings.System.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID
        );
    }

    /**
     * Take a persistable URI permission grant that has been offered. Once
     * taken, the permission grant will be remembered across device reboots.
     * Only URI permissions granted with
     * {@link Intent#FLAG_GRANT_PERSISTABLE_URI_PERMISSION} can be persisted. If
     * the grant has already been persisted, taking it again will touch
     * {@link UriPermission#getPersistedTime()}.
     *
     */
    public static void takePersistableUriPermission(Context context, Uri uri, Intent intent) {
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Check for the freshest data.
        context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }

    /**
     * Restart App
     */
    public static void restart() {
        Intent intent = AnywhereApplication.sContext.getPackageManager()
                .getLaunchIntentForPackage(AnywhereApplication.sContext.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            AnywhereApplication.sContext.startActivity(intent);
        }
    }

    /**
     *
     * Start recording log
     *
     * @param context Context
     */
    public static void startLogcat(Context context) {
        GlobalValues.sIsDebugMode = true;
        LogRecorder logRecorder = new LogRecorder.Builder(context)
                .setLogFolderName(context.getString(R.string.logcat))
                .setLogFileNameSuffix(com.blankj.utilcode.util.AppUtils.getAppName())
                .setLogFileSizeLimitation(256)
                .setLogLevel(LogRecorder.DEBUG)
                .setPID(android.os.Process.myPid())
                .build();
        LogRecorder.setInstance(logRecorder);
        NotifyUtils.createLogcatNotification(context);
        LogcatActivity.isStartCatching = true;
    }

    /**
     *
     * Send selected log file to my mailbox
     *
     * @param context Context
     * @param file Log file
     */
    public static void sendLogcat(Context context, File file) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        if (file != null) {
            emailIntent.setType("application/octet-stream");
            String[] emailReceiver = new String[]{"zhaobozhen2025@gmail.com"};

            String emailTitle = String.format("[%s] App Version Code: %s", context.getString(R.string.report_title), BuildConfig.VERSION_CODE);

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailReceiver);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
            String emailContent = context.getString(R.string.report_describe) + " \n\n" +
                    "App Version Name: " + BuildConfig.VERSION_NAME + "\n" +
                    "App Version Code: " + BuildConfig.VERSION_CODE + "\n" +
                    "Device: " + Build.MODEL + "\n" +
                    "Android Version: " + Build.VERSION.RELEASE;
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            //Filter Email Apps
            Intent queryIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(queryIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                            | PackageManager.GET_RESOLVED_FILTER);

            ArrayList<Intent> targetIntents = new ArrayList<>();
            for (ResolveInfo info : resolveInfos) {
                ActivityInfo ai = info.activityInfo;
                Intent intent = new Intent(emailIntent);
                intent.setPackage(ai.packageName);
                intent.setComponent(new ComponentName(ai.packageName, ai.name));
                targetIntents.add(intent);
            }

            Intent chooser = Intent.createChooser(targetIntents.remove(0), context.getString(R.string.report_select_mail_app));
            chooser.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
            context.startActivity(chooser);
        }
    }
}

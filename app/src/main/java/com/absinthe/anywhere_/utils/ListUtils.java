package com.absinthe.anywhere_.utils;

import android.content.ComponentName;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Sort the Anywhere- list
 */
public class ListUtils {
    public static List<AnywhereEntity> sort(List<AnywhereEntity> list) {
        switch (GlobalValues.sSortMode) {
            case Const.SORT_MODE_TIME_DESC:
                return sortByTimeDesc(list);
            case Const.SORT_MODE_TIME_ASC:
                return sortByTimeAsc(list);
            case Const.SORT_MODE_NAME_DESC:
                return sortByNameDesc(list);
            case Const.SORT_MODE_NAME_ASC:
                return sortByNameAsc(list);
        }
        return sortByTimeDesc(list);
    }

    private static List<AnywhereEntity> sortByTimeDesc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                -anywhereEntity.getTimeStamp().compareTo(t1.getTimeStamp()));
        return list;
    }

    private static List<AnywhereEntity> sortByTimeAsc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                anywhereEntity.getTimeStamp().compareTo(t1.getTimeStamp()));
        return list;
    }

    private static List<AnywhereEntity> sortByNameDesc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                -Collator.getInstance(Locale.getDefault()).compare(anywhereEntity.getAppName(), t1.getAppName()));
        return list;
    }

    private static List<AnywhereEntity> sortByNameAsc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                Collator.getInstance(Locale.getDefault()).compare(anywhereEntity.getAppName(), t1.getAppName()));
        return list;
    }

    public static List<AppListBean> sortAppListByNameAsc(List<AppListBean> list) {
        Collections.sort(list, (item, t1) ->
                Collator.getInstance(Locale.getDefault()).compare(item.getAppName(), t1.getAppName()));
        return list;
    }

    public static List<AppListBean> sortAppListByExported(List<AppListBean> list) {
        Collections.sort(list, (item, t1) -> {
            if (UiUtils.isActivityExported(AnywhereApplication.sContext,
                    new ComponentName(item.getPackageName(), item.getClassName()))) {
                return -1;
            } else if (UiUtils.isActivityExported(AnywhereApplication.sContext,
                    new ComponentName(t1.getPackageName(), t1.getClassName()))) {
                return 1;
            } else {
                return 0;
            }
        });

        return list;
    }
}

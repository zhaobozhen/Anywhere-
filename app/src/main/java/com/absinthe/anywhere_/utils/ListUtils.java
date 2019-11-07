package com.absinthe.anywhere_.utils;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;

import java.util.Collections;
import java.util.List;

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
                - anywhereEntity.getTimeStamp().compareTo(t1.getTimeStamp()));
        return list;
    }

    private static List<AnywhereEntity> sortByTimeAsc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                anywhereEntity.getTimeStamp().compareTo(t1.getTimeStamp()));
        return list;
    }

    private static List<AnywhereEntity> sortByNameDesc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                - anywhereEntity.getAppName().compareTo(t1.getAppName()));
        return list;
    }

    private static List<AnywhereEntity> sortByNameAsc(List<AnywhereEntity> list) {
        Collections.sort(list, (anywhereEntity, t1) ->
                anywhereEntity.getAppName().compareTo(t1.getAppName()));
        return list;
    }

    public static List<AppListBean> sortAppListByNameAsc(List<AppListBean> list) {
        Collections.sort(list, (item, t1) ->
                item.getAppName().compareTo(t1.getAppName()));
        return list;
    }
}

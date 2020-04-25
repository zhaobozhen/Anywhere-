package com.absinthe.anywhere_.utils

import android.content.ComponentName
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.model.AppListBean
import com.absinthe.anywhere_.model.PageEntity
import com.blankj.utilcode.util.Utils

/**
 * Sort the Anywhere- list
 */
object ListUtils {

    fun sortAppListByExported(list: List<AppListBean>): List<AppListBean> {
        list.sortedWith(Comparator { o1, o2 ->
            when {
                UiUtils.isActivityExported(Utils.getApp(),
                        ComponentName(o1.packageName, o1.className)) -> {
                    -1
                }
                UiUtils.isActivityExported(Utils.getApp(),
                        ComponentName(o2.packageName, o2.className)) -> {
                    1
                }
                else -> {
                    0
                }
            }
        })

        return list
    }

    fun getPageEntityByTitle(title: String): PageEntity? {
        AnywhereApplication.sRepository.allPageEntities.value?.let {
            for (pe in it) {
                if (pe.title == title) {
                    return pe
                }
            }
        }

        return null
    }
}
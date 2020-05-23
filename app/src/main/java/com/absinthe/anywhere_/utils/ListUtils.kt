package com.absinthe.anywhere_.utils

import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.model.database.PageEntity

/**
 * Sort the Anywhere- list
 */
object ListUtils {

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
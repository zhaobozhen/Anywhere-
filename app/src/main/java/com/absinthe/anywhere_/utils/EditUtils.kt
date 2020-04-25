package com.absinthe.anywhere_.utils

import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.model.AnywhereEntity

object EditUtils {
    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @param param2 param2
     * @return true if has same Anywhere-
     */
    fun hasSameAppName(param1: String, param2: String): Boolean {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            for (ae in it) {
                if (param1 == ae.param1 && param2 == ae.param2) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @param param2 param2
     * @return true if has same Anywhere-
     */
    fun hasSameAppNameEntity(param1: String, param2: String): AnywhereEntity? {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            for (ae in it) {
                if (param1 == ae.param1 && param2 == ae.param2) {
                    return ae
                }
            }
        }
        return null
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @return true if has same Anywhere-
     */
    @JvmStatic
    fun hasSameAppName(param1: String): Boolean {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            for (ae in it) {
                if (param1 == ae.param1) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @return true if has same Anywhere-
     */
    @JvmStatic
    fun hasSameAppNameEntity(param1: String): AnywhereEntity? {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            for (ae in it) {
                if (param1 == ae.param1) {
                    return ae
                }
            }
        }
        return null
    }
}
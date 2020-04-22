package com.absinthe.anywhere_.utils.manager

import androidx.annotation.Keep
import com.absinthe.anywhere_.model.Settings

@Keep
object IzukoHelper {

    init {
        System.loadLibrary("izuko")
    }

    val cipherKey: String
        external get

    val isHitagi: Boolean
        get() = isHitagi(Settings.sToken)

    @JvmStatic
    external fun checkSignature()

    external fun isHitagi(token: String?): Boolean

}
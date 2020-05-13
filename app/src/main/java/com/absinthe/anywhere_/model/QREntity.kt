package com.absinthe.anywhere_.model

import com.absinthe.anywhere_.interfaces.OnQRLaunchedListener

class QREntity internal constructor(var listener: OnQRLaunchedListener) {

    var pkgName: String? = null
    var clsName: String? = null
    var urlScheme: String? = null

    fun launch() {
        listener.onLaunched()
    }

    override fun toString(): String {
        return "QREntity: pkgName = $pkgName, clsName = $clsName, urlScheme = $urlScheme"
    }
}
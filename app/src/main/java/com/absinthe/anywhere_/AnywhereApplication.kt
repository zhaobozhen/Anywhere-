package com.absinthe.anywhere_

import android.app.Application
import android.content.Context
import com.absinthe.anywhere_.database.AnywhereRepository
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.utils.manager.IzukoHelper.checkSignature
import com.absinthe.anywhere_.utils.manager.ShizukuHelper
import com.absinthe.anywhere_.utils.timber.ReleaseTree
import com.absinthe.anywhere_.utils.timber.ThreadAwareDebugTree
import jonathanfinerty.once.Once
import me.weishu.reflection.Reflection
import timber.log.Timber

class AnywhereApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(ThreadAwareDebugTree())
        } else {
            checkSignature()
            Timber.plant(ReleaseTree())
        }

        sRepository = AnywhereRepository(this)
        GlobalValues.init(this)
        Once.initialise(this)
        Settings.init()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
        ShizukuHelper.bind(base)
    }

    companion object {
        lateinit var sRepository: AnywhereRepository
    }
}
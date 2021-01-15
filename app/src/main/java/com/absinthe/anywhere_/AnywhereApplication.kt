package com.absinthe.anywhere_

import android.app.Application
import android.content.Context
import com.absinthe.anywhere_.database.AnywhereRepository
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.utils.manager.IzukoHelper.checkSignature
import com.absinthe.anywhere_.utils.manager.PoliceMan
import com.absinthe.anywhere_.utils.timber.ReleaseTree
import com.absinthe.anywhere_.utils.timber.ThreadAwareDebugTree
import com.absinthe.libraries.utils.utils.Utility
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import io.michaelrocks.paranoid.Obfuscate
import jonathanfinerty.once.Once
import me.weishu.reflection.Reflection
import timber.log.Timber

@Obfuscate
class AnywhereApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(ThreadAwareDebugTree())
        } else {
            checkSignature()
            Timber.plant(ReleaseTree())
            AppCenter.start(this, "ec71d412-5886-4a99-89a7-805436b91671",
                    Analytics::class.java, Crashes::class.java)
        }

        PoliceMan.checkApplicationClass(this)
        PoliceMan.checkPMProxy(this)
        sRepository = AnywhereRepository(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        Reflection.unseal(base)
        Once.initialise(this)
        Settings.initMMKV(this)
        Settings.init(this)
        Utility.init(this)
    }

    companion object {
        lateinit var sRepository: AnywhereRepository
    }
}
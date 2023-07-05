package com.absinthe.anywhere_

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import cn.vove7.andro_accessibility_api.AccessibilityApi
import com.absinthe.anywhere_.database.AnywhereRepository
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.services.IzukoService
import com.absinthe.anywhere_.utils.manager.IzukoHelper.checkSignature
import com.absinthe.anywhere_.utils.manager.PoliceMan
import com.absinthe.anywhere_.utils.timber.ReleaseTree
import com.absinthe.anywhere_.utils.timber.ThreadAwareDebugTree
import com.absinthe.libraries.utils.utils.Utility
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import jonathanfinerty.once.Once
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.sui.Sui
import timber.log.Timber

class AnywhereApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(ThreadAwareDebugTree())
    } else {
      checkSignature()
      PoliceMan.checkApplicationClass(this)
      PoliceMan.checkPMProxy(this)
      Timber.plant(ReleaseTree())
      AppCenter.start(
        this, BuildConfig.APP_CENTER_SECRET,
        Analytics::class.java, Crashes::class.java
      )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      HiddenApiBypass.addHiddenApiExemptions("")
    }

    app = this
    Once.initialise(this)
    Settings.initMMKV(this)
    Settings.init()
    Utility.init(this)
    Sui.init(BuildConfig.APPLICATION_ID)
    AppCompatDelegate.setDefaultNightMode(Settings.getTheme())
    Global.start()
    sRepository = AnywhereRepository(this)

    AccessibilityApi.apply {
      BASE_SERVICE_CLS = IzukoService::class.java
      GESTURE_SERVICE_CLS = IzukoService::class.java
    }
  }

  companion object {
    lateinit var sRepository: AnywhereRepository
    lateinit var app: AnywhereApplication
  }
}

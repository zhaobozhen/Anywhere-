package com.absinthe.anywhere_.utils.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import java.lang.reflect.Field
import kotlin.system.exitProcess


private const val APPLICATION_CLASS_NAME = "AnywhereApplication"

object PoliceMan {

  fun checkApplicationClass(application: Application) {
    if (APPLICATION_CLASS_NAME != application.javaClass.simpleName) {
      exitProcess(0)
    }
  }

  @SuppressLint("PrivateApi")
  fun checkPMProxy(application: Application) {
    val realPMName = "android.content.pm.IPackageManager\$Stub\$Proxy"
    var currentPMName = ""

    try {
      // 被代理的对象是 PackageManager.mPM
      val packageManager: PackageManager = application.packageManager
      val mPMField: Field = packageManager.javaClass.getDeclaredField("mPM")
      mPMField.isAccessible = true
      val mPM: Any? = mPMField.get(packageManager)
      // 取得类名
      currentPMName = mPM?.javaClass?.name.orEmpty()
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      // 类名改变说明被代理了
      if (currentPMName != realPMName) {
        exitProcess(0)
      }
    }
  }
}

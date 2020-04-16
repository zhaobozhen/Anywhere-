package com.absinthe.anywhere_.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.Utils

class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        //设备管理可用
    }

    override fun onDisabled(context: Context, intent: Intent) {
        //设备管理不可用
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {}

    companion object {
        val componentName = ComponentName(Utils.getApp(), AdminReceiver::class.java)
    }
}
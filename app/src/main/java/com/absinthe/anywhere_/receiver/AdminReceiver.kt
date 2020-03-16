package com.absinthe.anywhere_.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        //设备管理可用
    }

    override fun onDisabled(context: Context, intent: Intent) {
        //设备管理不可用
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {}
}
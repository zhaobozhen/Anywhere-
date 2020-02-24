package com.absinthe.anywhere_.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        //设备管理可用
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        //设备管理不可用
    }

    @Override
    public void onPasswordChanged(@NonNull Context context, @NonNull Intent intent) {

    }
}

package com.kunangkunang.app.helper

import android.content.ComponentName
import android.content.Context

class DeviceAdminReceiver: android.app.admin.DeviceAdminReceiver() {
    fun getComponentName(context: Context): ComponentName {
        return ComponentName(
            context.applicationContext, DeviceAdminReceiver::class.java
        )
    }
}
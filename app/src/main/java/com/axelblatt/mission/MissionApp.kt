package com.axelblatt.mission

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.axelblatt.mission.notification.NotificationHelper

class MissionApp: Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false,
            getString(R.string.app_name),
            "Task Notifications"
        )
    }

}
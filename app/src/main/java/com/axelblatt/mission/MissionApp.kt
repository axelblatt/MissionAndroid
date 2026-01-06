package com.axelblatt.mission

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.DefaultEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.axelblatt.mission.notification.NotificationHelper

class MissionApp: Application() {

    override fun onCreate() {
        super.onCreate()
        EmojiCompat.init(BundledEmojiCompatConfig(this))
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false,
            getString(R.string.app_name),
            "Task Notifications"
        )
    }

}
package com.axelblatt.mission.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.axelblatt.mission.MainActivity
import com.axelblatt.mission.R

class NotificationHelper {

    companion object {
        fun createNotificationChannel(
            context: Context, importance: Int, showBadge: Boolean, name: String, description: String
        ) {

            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }

        fun showNotification(
            context: Context, title: String, message: String,
            bigText: String = "", notifyId: Int
        ) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

                // Building Intent for moving to exact task in the activity
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("task_id", notifyId)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
                    setSmallIcon(R.drawable.ic_stat_name)
                    setContentTitle(title)
                    setContentText(message)
                    setAutoCancel(true)
                    setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                    setContentIntent(pendingIntent)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }

                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(notifyId, notificationBuilder.build())
            }

        }

    }

}
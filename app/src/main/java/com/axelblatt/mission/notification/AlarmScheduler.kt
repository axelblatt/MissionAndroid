package com.axelblatt.mission.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.axelblatt.mission.R
import com.axelblatt.mission.data.Task
import java.util.Calendar
import java.util.Locale

class AlarmScheduler {

    companion object {

        fun scheduleAlarm(context: Context, task: Task, nextDay: Boolean = false) {

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
                type = "${task.id}-${context.packageName}-${context.getString(R.string.app_name)}"
                putExtra("task_id", task.id)
            }
            val alarmIntent = PendingIntent.getBroadcast(context, task.id, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val hour = task.time / 3600
            val minute = task.time % 3600

            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()
            datetimeToAlarm.set(Calendar.HOUR_OF_DAY, hour)
            datetimeToAlarm.set(Calendar.MINUTE, minute)
            datetimeToAlarm.set(Calendar.SECOND, 0)
            datetimeToAlarm.set(Calendar.MILLISECOND, 0)

            val today = Calendar.getInstance(Locale.getDefault())
            if (today.get(Calendar.HOUR_OF_DAY) * 60 + today.get(Calendar.MINUTE) >= hour * 60 + minute && !nextDay) {
//                Log.d("MISSION", "EARLIER: SCHEDULING NEXT DAY")
                datetimeToAlarm.roll(Calendar.DATE, 1)
            }

            if (nextDay) {
//                Log.d("MISSION", "NEXT DAY")
                datetimeToAlarm.timeInMillis = today.timeInMillis
//                datetimeToAlarm.set(Calendar.HOUR_OF_DAY, hour)
//                datetimeToAlarm.set(Calendar.MINUTE, minute)
//                datetimeToAlarm.set(Calendar.SECOND, 0)
//                datetimeToAlarm.set(Calendar.MILLISECOND, 0)
                datetimeToAlarm.roll(Calendar.DATE, 1)
            }

            alarmMgr.cancel(alarmIntent)
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis,
//               (1000 * 60 * 60 * 24).toLong(),
                alarmIntent
            )

        }

        fun cancelAlarm(context: Context, taskId: Int) {

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
                type = "${taskId}-${context.packageName}-${context.getString(R.string.app_name)}"
                putExtra("task_id", taskId)
            }
            val alarmIntent = PendingIntent.getBroadcast(context, taskId, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            alarmMgr.cancel(alarmIntent)

        }

    }

}
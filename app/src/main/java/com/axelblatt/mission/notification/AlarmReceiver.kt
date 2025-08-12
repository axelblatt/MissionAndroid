package com.axelblatt.mission.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.axelblatt.mission.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        CoroutineScope(Dispatchers.IO).launch {

            if (context != null && intent != null) {

                if (intent.extras != null) {
                    val db = TaskDatabase.getDatabase(context)
                    val dao = db.taskDao()
                    Log.d("MISSION", "ID " + intent.extras!!.getInt("task_id", -1).toString())
                    val task =
                        dao.getTaskById(intent.extras!!.getInt("task_id", -1))[0]

                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    if (task.marked != calendar.timeInMillis) {
                        NotificationHelper.showNotification(
                            context = context, title = task.name,
                            message = "It's time to do a task",
                            notifyId = task.id
                        )
                    }

                    AlarmScheduler.scheduleAlarm(context, task, true)

                } else {
                    Log.d("MISSION", "ID -1")
                    NotificationHelper.showNotification(
                        context = context, title = "Task!",
                        message = "It's time to do a task",
                        notifyId = 1001
                    )
                }
            }

        }

    }

}
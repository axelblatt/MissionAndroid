package com.axelblatt.mission.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.axelblatt.mission.R
import com.axelblatt.mission.data.Task
import com.axelblatt.mission.data.TaskViewModel
import com.axelblatt.mission.notification.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale", "UnusedContentLambdaTargetStateParameter", "SimpleDateFormat")
@Composable
fun MainScreen(
    modifier: Modifier,
    taskViewModel: TaskViewModel,
    defaultTaskId: Int = -1
) {
    val localContext = LocalContext.current
    val localDensity = LocalDensity.current

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xffb0c6ff),
        contentColor = Color(0xff142e5f)
    )

    val buttonColorsChecked = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF66BB6A),
        contentColor = Color(0xff142e5f)
    )

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val tasks by taskViewModel.readAllData.observeAsState(initial = emptyList())
    var rememberTaskId by remember { mutableIntStateOf(defaultTaskId) }
    var task by remember { mutableStateOf(getTaskById(tasks, rememberTaskId)) }
    LaunchedEffect(tasks) {
        task = getTaskById(tasks, rememberTaskId)
    }

    val launchedFlag = remember { mutableStateOf(false) }
    if (!launchedFlag.value && tasks.isNotEmpty()) {
        launchedFlag.value = true
        for (i in tasks) {
            if (i.notification)
                AlarmScheduler.scheduleAlarm(localContext, i)
            else
                AlarmScheduler.cancelAlarm(localContext, i.id)
        }

    }

    val addingTaskFlag = remember { mutableStateOf(false) }
    val editingTaskFlag = remember { mutableStateOf(false) }
    val deletingTaskFlag = remember { mutableStateOf(false) }
    val resettingTaskFlag = remember { mutableStateOf(false) }
    val infoFlag = remember { mutableStateOf(false) }
    val notificationsDialogFlag = remember { mutableStateOf(false) }

    var checked by remember { mutableStateOf(false) }

    var toastShown by remember { mutableStateOf(false) }
    if (task.id == 0) checked = false
    else if (task.marked == calendar.timeInMillis) checked = true
    else if (calendar.timeInMillis - task.marked <= 24 * 60 * 60 * 1000L) checked = false
    if (calendar.timeInMillis - task.marked > 24 * 60 * 60 * 1000L && task.id != 0) {
        checked = false
        taskViewModel.resetTask(task, calendar.timeInMillis)
        if (!toastShown && task.marked != task.start) {
            Toast.makeText(localContext,
                "Task has been reset due to skipping the previous day",
                Toast.LENGTH_LONG).show()
            toastShown = true
        }
    } else toastShown = false

    var notificationStatus by remember { mutableStateOf(false) }
    notificationStatus = task.notification

    // Notifications and Time Statements
    val is24Hour = DateFormat.is24HourFormat(localContext)
    val timeState = rememberTimePickerState(
        is24Hour = is24Hour,
        initialHour = if (task.id != 0) task.time / 3600 else 0,
        initialMinute = if (task.id != 0) task.time % 3600 else 0
    )

    if (addingTaskFlag.value) {
        AddDialog(addingTaskFlag, taskViewModel, buttonColors, calendar, modifier, editingTaskFlag, task)
    }
    if (deletingTaskFlag.value) DeleteDialog(deletingTaskFlag, task.id, taskViewModel, buttonColors)
    if (resettingTaskFlag.value) ResetDialog(resettingTaskFlag, task,
        calendar.timeInMillis, taskViewModel, buttonColors)
    if (infoFlag.value) InfoDialog(infoFlag, buttonColors, modifier, localContext)
    if (notificationsDialogFlag.value) {
        timeState.is24hour = DateFormat.is24HourFormat(localContext)
        timeState.hour = task.time / 60
        timeState.minute = task.time % 60
        TimeDialog(task.id, notificationsDialogFlag, taskViewModel,
            timeState, localContext, modifier)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            if (!notificationStatus) {
                taskViewModel.updateNotificationStatus(task.id, true)
                AlarmScheduler.scheduleAlarm(localContext, task)
            } else {
                taskViewModel.updateNotificationStatus(task.id, false)
                AlarmScheduler.cancelAlarm(localContext, task.id)
            }

        } else {
            Toast.makeText(localContext,
                "Turn on notifications in settings",
                Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Info Button
        Box(
            modifier
                .align(Alignment.End)
                .size(56.dp),
            contentAlignment = Alignment.BottomStart) {
            IconButton(onClick = { infoFlag.value = true}) {
                Icon(Icons.Filled.Info, "Info", modifier.size(32.dp))
            }
        }

        Spacer(modifier.size(86.dp))

        Column(
            modifier
                .height(64.dp + with(localDensity) { 32.sp.toDp() })
                .fillMaxWidth()) {
            Text(
                text = task.name,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.align(Alignment.CenterHorizontally),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 36.sp
            )
        }

        // Progress
        CircularProgressBar(task = task, modifier = modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier.size(32.dp))

        // Task Options
        Row(modifier.align(Alignment.CenterHorizontally)) {
            IconButton(onClick = {
                if (task.id != 0) {
                    if (task.id == tasks[0].id && tasks.size > 1) {
                        rememberTaskId = tasks[1].id
                    } else for (t in tasks) {
                        if (task.id == t.id) break
                        else rememberTaskId = t.id
                    }
                    deletingTaskFlag.value = true
                }
            }) {
                Icon(Icons.Filled.Delete, "Delete", modifier.size(32.dp))
            }
            Spacer(modifier.size(16.dp))

            IconButton(onClick = {
                if (!checked) taskViewModel.markTask(task.id)
                else taskViewModel.unmarkTask(task.id)
            }) {
                Icon(
                    imageVector = if (checked) ImageVector.vectorResource(R.drawable.marked)
                        else Icons.Outlined.Done,
                    contentDescription = "Mark",
                    modifier = modifier.size(32.dp),
                    tint = if (checked) Color.Unspecified else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier.size(16.dp))
            IconButton(onClick = {
                if (task.id != 0) resettingTaskFlag.value = true
            }) {
                Icon(Icons.Filled.Refresh, "Reset", modifier.size(32.dp))
            }
        }
        // Spacer(modifier.size(46.dp))
        Spacer(modifier.size(15.dp))

        // Notifications & tasks settings
        Row(modifier.align(Alignment.CenterHorizontally)) {
            Button(onClick = {
                if (task.id == 0) return@Button
                Log.d("MISSION", "ID " + task.id.toString() + " PLANNED")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    // REQUEST_CODE_POST_NOTIFICATIONS
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            localContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) -> {
                            // Permission granted
                            if (!notificationStatus) {
                                taskViewModel.updateNotificationStatus(task.id, true)
                                AlarmScheduler.scheduleAlarm(localContext, task)
                            } else {
                                taskViewModel.updateNotificationStatus(task.id, false)
                                AlarmScheduler.cancelAlarm(localContext, task.id)
                            }
                        } else -> {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            },
                modifier
                    .size(40.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = if (notificationStatus) buttonColorsChecked else buttonColors
            ) {
                Icon(Icons.Filled.Notifications, "Notification", modifier.size(22.dp))
            }
            Spacer(modifier.size(10.dp))
            Button(onClick = {
                if (task.id != 0) notificationsDialogFlag.value = true
            },
                modifier
                    .height(40.dp)
                    .width(120.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = buttonColors
            ) {
                Text(
                    if (task.id != 0) formatTime(task.time / 3600, task.time % 3600, is24Hour)
                    else "No tasks", fontSize = 20.sp)
            }
            Spacer(modifier.size(10.dp))
            Button(onClick = {
                if (task.id == 0) return@Button
                editingTaskFlag.value = true
                addingTaskFlag.value = true
            },
                modifier
                .size(40.dp)
                .shadow(5.dp, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = buttonColors
            ) {
                Icon(Icons.Filled.Edit, "Edit", modifier.size(22.dp))
            }
        }

        Spacer(modifier.size(23.dp))

        // Tasks
        LazyRow(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            itemsIndexed(tasks){_, t ->
                Spacer(modifier.size(26.dp))
                val offset by animateIntOffsetAsState(
                    targetValue = if (task.id == t.id) {
                        IntOffset(0, -10)
                    } else { IntOffset.Zero },
                    label = "offset")
                Button(
                    onClick = {
                        task = getTaskById(tasks, t.id)
                        rememberTaskId = task.id
                    },
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = modifier
                        .size(64.dp)
                        .offset { offset }
                        .shadow(5.dp, shape = RoundedCornerShape(16.dp)),
                    colors = if (t.marked == calendar.timeInMillis) buttonColorsChecked
                        else buttonColors
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = t.emoji,
                            fontSize = 28.sp
                        )
                    }
                }
            }
            item {
                Spacer(modifier.size(26.dp))
                Button(
                    onClick = {
                        rememberTaskId = if (tasks.isEmpty()) -1 else -2
                        addingTaskFlag.value = true
                    },
                    modifier
                        .size(64.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = buttonColors
                ) {
                    Icon(Icons.Filled.Add, "Add", modifier.size(36.dp))
                }
                Spacer(modifier.size(26.dp))
            }
        }

    }

}

@SuppressLint("DefaultLocale")
@Composable
fun CircularProgressBar(
    task: Task,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 50f,
    backgroundColor: Color = Color.Gray,
    progressColor: Color = Color.Yellow // MaterialTheme.colors.primary
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        val marked = (task.marked - task.start) / (1000 * 3600 * 24)
        val end = (task.end - task.start) / (1000 * 3600 * 24)
        var progress = marked.toFloat() / end
        var daysText = "$marked / $end"
        var progressText = String.format("%.2f", progress * 100) + "%"
        if (task.id == 0) {
            progress = 0f
            daysText = "0 / 0"
            progressText = "Add a new task"
        }

        val animatable = remember { Animatable(0f) }
        LaunchedEffect(progress) {
            animatable.animateTo(
                targetValue = progress,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }

        Canvas(modifier.size(200.dp)) {
            val diameter = size.minDimension
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(diameter, diameter),
                style = Stroke(strokeWidth)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatable.value,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(diameter, diameter),
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier.size(16.dp))
            Text(daysText, fontSize = 28.sp)
            Text(progressText)
        }
    }
}

fun getTaskById(tasks: List<Task>, id: Int): Task {
    if (tasks.isNotEmpty()) {
        if (id == -1) return tasks[0]
        if (id == -2) return tasks[tasks.size - 1]
        for (task in tasks) if (task.id == id) return task
    }
    return Task(0,"No Tasks", 0, 0, 0, "")
}

fun formatTime(hour: Int, minute: Int, is24Hour: Boolean): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }

    val timeFormat = if (is24Hour) {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    return timeFormat.format(calendar.time)
}
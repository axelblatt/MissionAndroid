package com.axelblatt.mission.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.axelblatt.mission.data.TaskViewModel
import com.axelblatt.mission.notification.AlarmScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    taskId: Int,
    timeDialog: MutableState<Boolean>,
    taskViewModel: TaskViewModel,
    timeState: TimePickerState,
    localContext: Context,
    modifier: Modifier
) {
    BasicAlertDialog(
        onDismissRequest = {timeDialog.value = false},
        modifier = modifier.fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier.size(12.dp))
            TimePicker(timeState)
            Spacer(modifier.size(12.dp))
            Button(onClick = {
                timeDialog.value = false
                taskViewModel.updateNotificationTask(taskId, timeState.hour * 60 + timeState.minute)
                AlarmScheduler.cancelAlarm(localContext, taskId)
            }) { Text("OK") }
            Spacer(modifier.size(12.dp))
        }
    }
}
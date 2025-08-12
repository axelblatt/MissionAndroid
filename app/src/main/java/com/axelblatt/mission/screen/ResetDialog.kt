package com.axelblatt.mission.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import com.axelblatt.mission.data.Task
import com.axelblatt.mission.data.TaskViewModel

@Composable
fun ResetDialog(
    resettingTaskFlag: MutableState<Boolean>,
    task: Task,
    today: Long,
    taskViewModel: TaskViewModel,
    buttonColors: ButtonColors
) {
    AlertDialog(
        onDismissRequest = { resettingTaskFlag.value = false},
        title = { Text(text = "Reset task") },
        text = { Text("Are you sure you want reset the task?") },
        confirmButton = {
            Button(
                onClick = {
                    taskViewModel.resetTask(task, today)
                    resettingTaskFlag.value = false
                },
                colors = buttonColors
            ) {
                Text("OK", fontSize = 16.sp)
            }
        }
    )
}
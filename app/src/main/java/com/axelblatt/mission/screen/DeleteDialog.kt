package com.axelblatt.mission.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import com.axelblatt.mission.data.TaskViewModel

@Composable
fun DeleteDialog(
    deletingTaskFlag: MutableState<Boolean>,
    taskId: Int,
    taskViewModel: TaskViewModel,
    buttonColors: ButtonColors
) {
    AlertDialog(
        onDismissRequest = { deletingTaskFlag.value = false},
        title = { Text(text = "Delete task") },
        text = { Text("Are you sure you want delete the task?") },
        confirmButton = {
            Button(
                onClick = {
                    taskViewModel.deleteTask(taskId)
                    deletingTaskFlag.value = false
                },
                colors = buttonColors
            ) {
                Text("OK", fontSize = 16.sp)
            }
        }
    )
}
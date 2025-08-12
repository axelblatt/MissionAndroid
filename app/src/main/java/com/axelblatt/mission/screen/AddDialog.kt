package com.axelblatt.mission.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.emoji2.emojipicker.EmojiViewItem
import com.axelblatt.mission.R
import com.axelblatt.mission.data.Task
import com.axelblatt.mission.data.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialog(
    addingTaskFlag: MutableState<Boolean>,
    taskViewModel: TaskViewModel,
    buttonColors: ButtonColors,
    calendar: Calendar,
    modifier: Modifier = Modifier,
    editingTaskFlag: MutableState<Boolean>,
    task: Task
) {
    val emojiViewItem = remember { mutableStateOf(EmojiViewItem(
        if (editingTaskFlag.value) task.emoji else "❓", emptyList())) }
    val emojiDialog = remember { mutableStateOf(false) }
    val textStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        fontSize = 26.sp,
        fontWeight = FontWeight(700),
        fontFamily = FontFamily(Font(R.font.roboto)),
        letterSpacing = 0.5.sp
    )
    var nameWidth by remember { mutableStateOf(0.dp) }
    var daysWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    if (emojiDialog.value) EmojiDialog(emojiDialog, emojiViewItem, modifier)

    val taskName = remember {
        mutableStateOf(if (editingTaskFlag.value) task.name else "Task Name")
    }
    val days = remember {
        mutableStateOf(
        if (editingTaskFlag.value) ((task.end - task.start) / (1000 * 3600 * 24)).toString()
        else "90")
    }
    BasicAlertDialog(
        onDismissRequest = {
            addingTaskFlag.value = false
            editingTaskFlag.value = false
        },
        modifier = modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier.size(12.dp))
            Button(
                onClick = { emojiDialog.value = true },
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = modifier
                    .size(48.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(16.dp)),
                colors = buttonColors
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = emojiViewItem.value.emoji,
                        fontSize = 26.sp
                    )
                }
            }
            Spacer(modifier.size(12.dp))
            BasicTextField(
                modifier = modifier,
                value = taskName.value,
                onValueChange = { taskName.value = it },
                singleLine = true,
                textStyle = textStyle,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    val lineWidth = textLayoutResult.getLineRight(0) - textLayoutResult.getLineLeft(0)
                    nameWidth = with(density) { lineWidth.toDp() }
                },
                decorationBox = { innerTextField ->
                    Box {
                        innerTextField()
                        Box(
                            modifier
                                .width(nameWidth)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.onSurface)
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            )
            Spacer(modifier.size(12.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                BasicTextField(
                    modifier = modifier.width(daysWidth + 15.dp),
                    value = days.value,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 3)
                            days.value = it
                    },
                    singleLine = true,
                    textStyle = textStyle,
                    onTextLayout = { textLayoutResult: TextLayoutResult ->
                        val lineWidth = textLayoutResult.getLineRight(0) - textLayoutResult.getLineLeft(0)
                        daysWidth = with(density) { lineWidth.toDp() }
                    },
                    decorationBox = { innerTextField ->
                        Box {
                            innerTextField()
                            Box(
                                modifier
                                    .width(daysWidth)
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onSurface)
                                    .align(Alignment.BottomCenter)
                            )
                        }
                    }
                )
                Text(
                    text = "days",
                    fontSize = 26.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier.size(12.dp))
            Button(
                onClick = {
                    if (days.value.toInt() > 0) {
                        val start = calendar.timeInMillis - 24 * 60 * 60 * 1000L
                        var name = taskName.value.trim()
                        while (name.contains("  ")) name = name.replace("  ", " ")
                        if (editingTaskFlag.value) {
                            taskViewModel.editTask(
                                task.id, emojiViewItem.value.emoji,
                                name, task.start + days.value.toInt() * 24 * 60 * 60 * 1000L
                            )
                        } else {
                            taskViewModel.addTask(
                                Task(
                                    name = name,
                                    start = start,
                                    end = start + days.value.toInt() * 24 * 60 * 60 * 1000L,
                                    marked = start,
                                    emoji = emojiViewItem.value.emoji
                                )
                            )
                        }
                    }
                    addingTaskFlag.value = false
                    editingTaskFlag.value = false
                },
                modifier
                    .size(48.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                colors = buttonColors
            ) {
                Icon(if (editingTaskFlag.value) Icons.Filled.Check else Icons.Filled.Add, "Add", modifier.size(27.dp))
            }
            Spacer(modifier.size(12.dp))
        }
    }
}
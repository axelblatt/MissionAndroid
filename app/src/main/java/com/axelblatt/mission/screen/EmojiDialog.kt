package com.axelblatt.mission.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.EmojiViewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiDialog(
    emojiDialog: MutableState<Boolean>,
    emojiViewItem: MutableState<EmojiViewItem>,
    modifier: Modifier
) {
    BasicAlertDialog(
        onDismissRequest = {emojiDialog.value = false},
        modifier = modifier.fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier.size(8.dp).background(Color.White))
            Text(text = emojiViewItem.value.emoji, fontSize = 32.sp)
            AndroidView(
                modifier = modifier.fillMaxWidth().fillMaxHeight(0.9f),
                factory = {
                    EmojiPickerView(it)
                        .apply {
                            emojiGridColumns = 9
                            emojiGridRows = 6f
                            setOnEmojiPickedListener { item ->
                                emojiViewItem.value = item
                            }
                        }
                }
            )
            Button(onClick = { emojiDialog.value = false }) { Text("OK") }
        }
    }
}
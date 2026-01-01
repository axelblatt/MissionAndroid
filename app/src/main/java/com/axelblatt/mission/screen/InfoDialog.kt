package com.axelblatt.mission.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axelblatt.mission.R
import androidx.core.net.toUri

@Composable
fun InfoDialog(
    infoFlag: MutableState<Boolean>,
    buttonColors: ButtonColors,
    modifier: Modifier,
    context: Context
) {
    val ver = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    AlertDialog(
        onDismissRequest = { infoFlag.value = false },
        title = {
            Row(modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Mission")
                    Text(text = "Daily habit tracker,\nas " +
                            "simple as possible.", fontSize = 16.sp, lineHeight = 18.sp)
                    Text(text = "version $ver", fontSize = 16.sp)
                }
                Image(
                    painterResource(R.drawable.logo),
                    "Logo",
                    modifier.clip(shape = RoundedCornerShape(16.dp))
                        .size(64.dp)
                )
            }

        },
        text = {
            Column {
                Text("Author: Axelblatt")
                Row {
                    // GitHub
                    IconButton(onClick = {
                        val url = "https://github.com/axelblatt/MissionAndroid"
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                if (isSystemInDarkTheme()) R.drawable.github_mark_white
                                else R.drawable.github_mark
                            ),
                            contentDescription = "GitHub Logo",
                            modifier = modifier.size(26.dp)
                        )
                    }
                }


            }

        },
        confirmButton = {
            Button(
                onClick = { infoFlag.value = false },
                colors = buttonColors
            ) {
                Text("OK", fontSize = 16.sp)
            }
        }
    )
}
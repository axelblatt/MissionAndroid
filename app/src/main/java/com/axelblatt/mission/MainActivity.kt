package com.axelblatt.mission

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.axelblatt.mission.data.TaskViewModel
import com.axelblatt.mission.screen.MainScreen
import com.axelblatt.mission.ui.theme.MissionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskViewModel: TaskViewModel by viewModels()
        val modifier = Modifier
        val defaultTaskId = intent.getIntExtra("task_id", -1)
        Log.d("MISSION", "NOTIFICATION $defaultTaskId")
        setContent {
            MissionTheme {
                Surface(modifier = modifier.fillMaxSize()) {
                    MainScreen(modifier, taskViewModel, defaultTaskId)
                }
            }
        }
    }

}
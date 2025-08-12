package com.axelblatt.mission.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val start: Long = 0,
    val end: Long = 0,
    var marked: Long = 0,
    val emoji: String = "",
    var notification: Boolean = false,
    var time: Int = 12*60,
)
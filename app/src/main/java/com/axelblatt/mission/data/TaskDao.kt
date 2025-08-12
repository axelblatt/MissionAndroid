package com.axelblatt.mission.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Query("UPDATE task SET emoji = :taskEmoji, name = :taskName, `end` = :taskEnd WHERE id = :taskId")
    suspend fun editTask(taskId: Int, taskEmoji: String, taskName: String, taskEnd: Long)

    @Query("SELECT * FROM task ORDER BY id ASC")
    fun readAllData(): LiveData<List<Task>>

    @Query("SELECT COUNT(*) FROM task")
    fun getCount(): Int

//    @Query("SELECT * FROM task WHERE id = :taskId")
//    fun getTaskByIdBR(taskId: Int): List<Task>

    @Query("SELECT MAX(id) FROM task")
    fun getMaxId(): Int?

    @Query("SELECT MIN(id) FROM task")
    fun getMinId(): Int?

    @Query("DELETE FROM task WHERE id = :taskId")
    fun deleteTask(taskId: Int)

    @Query("UPDATE task SET start = :start, `end` = :end, marked = :start WHERE id = :taskId")
    fun resetTask(taskId: Int, start: Long, end: Long)

    @Query("UPDATE task SET marked = marked + 86400000 WHERE id = :taskId ")
    fun markTask(taskId: Int)

    @Query("UPDATE task SET marked = marked - 86400000 WHERE id = :taskId")
    fun unmarkTask(taskId: Int)

    @Query("UPDATE task SET time = :timeValue, notification = 0 WHERE id = :taskId")
    fun updateNotificationTask(taskId: Int, timeValue: Int)

    @Query("UPDATE task SET notification = :status WHERE id = :taskId")
    fun updateNotificationStatus(taskId: Int, status: Boolean)

    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskById(taskId: Int): List<Task>

}
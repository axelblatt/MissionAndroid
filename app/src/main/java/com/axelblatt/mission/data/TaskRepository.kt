package com.axelblatt.mission.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val readAllData: LiveData<List<Task>> = taskDao.readAllData()

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun editTask(taskId: Int, taskEmoji: String, taskName: String, taskEnd: Long) {
        taskDao.editTask(taskId, taskEmoji, taskName, taskEnd)
    }

    fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    fun resetTask(task: Task, start: Long) {
        val taskId = task.id
        val end = start - 24 * 60 * 60 * 1000L + (task.end - task.start)
        taskDao.resetTask(taskId, start - 24 * 60 * 60 * 1000L, end)
    }

    fun markTask(taskId: Int) {
        taskDao.markTask(taskId)
    }

    fun unmarkTask(taskId: Int) {
        taskDao.unmarkTask(taskId)
    }

    fun updateNotificationTask(taskId: Int, timeValue: Int) {
        taskDao.updateNotificationTask(taskId, timeValue)
    }

    fun updateNotificationStatus(taskId: Int, status: Boolean) {
        taskDao.updateNotificationStatus(taskId, status)
    }

}
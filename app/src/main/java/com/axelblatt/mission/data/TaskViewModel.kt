package com.axelblatt.mission.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = taskDao.readAllData()

    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun editTask(taskId: Int, taskEmoji: String, taskName: String, taskEnd: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.editTask(taskId, taskEmoji, taskName, taskEnd)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskId)
        }
    }

    fun resetTask(task: Task, today: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetTask(task, today)
        }
    }

    fun markTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markTask(taskId)
        }
    }

    fun unmarkTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unmarkTask(taskId)
        }
    }

    fun updateNotificationTask(taskId: Int, timeValue: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNotificationTask(taskId, timeValue)
        }
    }

    fun updateNotificationStatus(taskId: Int, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNotificationStatus(taskId, status)
        }
    }

}